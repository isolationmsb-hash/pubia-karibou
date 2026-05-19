package com.karibou.pubia.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Telecharge une video depuis une URL et l'enregistre dans la galerie
 * via MediaStore — pas besoin de permission WRITE_EXTERNAL_STORAGE
 * sur API 29+.
 */
@Singleton
class VideoDownloader @Inject constructor(
    private val httpClient: OkHttpClient
) {

    suspend fun downloadToGallery(
        context: Context,
        videoUrl: String,
        title: String = "PubIA-Karibou-${System.currentTimeMillis()}"
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder().url(videoUrl).build()
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) error("HTTP ${response.code}")

            val resolver = context.contentResolver
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val details = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, "$title.mp4")
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/PubIA-Karibou")
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }
            }

            val uri = resolver.insert(collection, details)
                ?: error("Impossible de creer le fichier dans la galerie")

            response.body?.byteStream()?.use { input ->
                resolver.openOutputStream(uri)?.use { output ->
                    input.copyTo(output)
                }
            } ?: error("Reponse vide")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                details.clear()
                details.put(MediaStore.Video.Media.IS_PENDING, 0)
                resolver.update(uri, details, null, null)
            }

            uri.toString()
        }
    }
}
