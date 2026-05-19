package com.karibou.pubia.data.repository

import com.karibou.pubia.data.local.AdProjectDao
import com.karibou.pubia.data.local.AdProjectEntity
import com.karibou.pubia.data.local.ImageStorageManager
import com.karibou.pubia.data.remote.BackendApi
import com.karibou.pubia.data.remote.dto.GenerateAdResponse
import com.karibou.pubia.data.remote.dto.ImproveScriptRequest
import com.karibou.pubia.data.remote.dto.JobStatusResponse
import com.karibou.pubia.domain.model.AdFormat
import com.karibou.pubia.domain.model.AdProject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour les projets de pub. Combine la persistance Room et le
 * stockage fichier (images/videos). En Phase 3+ ajoutera l'appel reseau.
 */
@Singleton
class AdProjectRepository @Inject constructor(
    private val dao: AdProjectDao,
    private val imageStorage: ImageStorageManager,
    private val backendApi: BackendApi
) {

    fun observeAll(): Flow<List<AdProject>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: Long): AdProject? = dao.getById(id)?.toDomain()

    suspend fun create(project: AdProject): Long {
        val now = System.currentTimeMillis()
        val withTimestamps = project.copy(createdAt = now, updatedAt = now)
        return dao.insert(AdProjectEntity.fromDomain(withTimestamps))
    }

    suspend fun update(project: AdProject) {
        val updated = project.copy(updatedAt = System.currentTimeMillis())
        dao.update(AdProjectEntity.fromDomain(updated))
    }

    /**
     * Supprime le projet et les fichiers associes (avatar, produit, video).
     */
    suspend fun delete(project: AdProject) {
        project.avatarPath?.let { imageStorage.delete(it) }
        project.productPath?.let { imageStorage.delete(it) }
        project.videoPath?.let { imageStorage.delete(it) }
        dao.deleteById(project.id)
    }

    /**
     * Persiste les bytes compresses d'une image dans la categorie
     * specifiee et retourne le chemin absolu.
     */
    suspend fun storeImage(category: String, bytes: ByteArray): String =
        imageStorage.saveImage(category, bytes)

    // === Backend API ===

    /**
     * Envoie le projet au backend pour generation. Les images sont uploadees
     * en multipart depuis les fichiers persistes dans filesDir.
     */
    suspend fun startGeneration(project: AdProject): GenerateAdResponse {
        val avatarFile = File(requireNotNull(project.avatarPath) { "avatarPath null" })
        val productFile = File(requireNotNull(project.productPath) { "productPath null" })

        val jpegType = "image/jpeg".toMediaType()
        val textType = "text/plain".toMediaType()

        return backendApi.generateAd(
            avatar = MultipartBody.Part.createFormData(
                name = "avatar",
                filename = avatarFile.name,
                body = avatarFile.asRequestBody(jpegType)
            ),
            product = MultipartBody.Part.createFormData(
                name = "product",
                filename = productFile.name,
                body = productFile.asRequestBody(jpegType)
            ),
            script = project.script.toRequestBody(textType),
            durationSeconds = project.durationSeconds.toString().toRequestBody(textType),
            format = project.format.name.toRequestBody(textType),
            voiceId = null
        )
    }

    suspend fun pollStatus(jobId: String): JobStatusResponse =
        backendApi.getStatus(jobId)

    suspend fun improveScript(rawScript: String, durationSeconds: Int): String =
        backendApi.improveScript(ImproveScriptRequest(rawScript, durationSeconds)).script
}
