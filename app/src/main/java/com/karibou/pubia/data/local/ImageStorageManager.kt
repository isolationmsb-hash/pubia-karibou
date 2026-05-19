package com.karibou.pubia.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sauvegarde les images compressees dans le stockage interne de l'app
 * (context.filesDir). Pas de permission requise, isole des autres apps,
 * efface a la desinstallation.
 *
 * Categorie possible : "avatar", "product", "video"...
 */
@Singleton
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveImage(category: String, bytes: ByteArray): String =
        withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, category).apply { mkdirs() }
            val file = File(dir, "${UUID.randomUUID()}.jpg")
            file.writeBytes(bytes)
            file.absolutePath
        }

    suspend fun delete(path: String) = withContext(Dispatchers.IO) {
        runCatching { File(path).delete() }
    }

    fun fileFor(path: String): File = File(path)
}
