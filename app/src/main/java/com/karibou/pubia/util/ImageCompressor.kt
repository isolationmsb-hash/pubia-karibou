package com.karibou.pubia.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

/**
 * Compresseur d'image. Respecte les contraintes du brief (section 7) :
 *  - max 1920px cote long
 *  - JPEG qualite 85
 *  - prise en compte de l'orientation EXIF
 *
 * IO sur Dispatchers.IO. Retourne des bytes prets a etre ecrits ou uploades.
 *
 * NOTE : androidx.exifinterface est inclus via Coil transitively. Si l'import
 * echoue, ajouter "androidx.exifinterface:exifinterface:1.3.7" dans libs.versions.toml.
 */
class ImageCompressor @Inject constructor() {

    suspend fun compress(context: Context, sourceUri: Uri): ByteArray =
        withContext(Dispatchers.IO) {
            // 1. Decoder seulement les bounds pour calculer le sample size
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(sourceUri).use { input ->
                BitmapFactory.decodeStream(input, null, boundsOptions)
            }
            val sampleSize = computeSampleSize(
                boundsOptions.outWidth,
                boundsOptions.outHeight,
                Constants.IMAGE_MAX_DIMENSION
            )

            // 2. Decoder la bitmap a la taille reduite
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val rawBitmap = context.contentResolver.openInputStream(sourceUri).use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            } ?: error("Impossible de decoder l'image source")

            // 3. Corriger l'orientation EXIF (les selfies sortent souvent rotates)
            val orientedBitmap = applyExifOrientation(context, sourceUri, rawBitmap)

            // 4. Redimensionner exactement si encore trop grand
            val resized = ensureMaxDimension(orientedBitmap, Constants.IMAGE_MAX_DIMENSION)

            // 5. Encoder en JPEG qualite 85
            val output = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_JPEG_QUALITY, output)

            // 6. Liberer les bitmaps intermediaires
            if (orientedBitmap !== rawBitmap) rawBitmap.recycle()
            if (resized !== orientedBitmap) orientedBitmap.recycle()
            resized.recycle()

            output.toByteArray()
        }

    private fun computeSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var sample = 1
        while (width / sample > maxDimension * 2 || height / sample > maxDimension * 2) {
            sample *= 2
        }
        return sample
    }

    private fun ensureMaxDimension(src: Bitmap, maxDimension: Int): Bitmap {
        val longSide = maxOf(src.width, src.height)
        if (longSide <= maxDimension) return src
        val scale = maxDimension.toFloat() / longSide.toFloat()
        val newW = (src.width * scale).toInt()
        val newH = (src.height * scale).toInt()
        return Bitmap.createScaledBitmap(src, newW, newH, true)
    }

    private fun applyExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        val orientation = readExifOrientation(context, uri)
        if (orientation == ExifInterface.ORIENTATION_NORMAL ||
            orientation == ExifInterface.ORIENTATION_UNDEFINED) return bitmap

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun readExifOrientation(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri).use { input: InputStream? ->
                input?.let { ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                ) } ?: ExifInterface.ORIENTATION_NORMAL
            }
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }
}
