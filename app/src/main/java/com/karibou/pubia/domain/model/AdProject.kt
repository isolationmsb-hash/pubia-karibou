package com.karibou.pubia.domain.model

/**
 * Statut d'un projet de publicite dans le parcours.
 */
enum class AdProjectStatus {
    DRAFT,          // En cours de creation (wizard)
    GENERATING,     // Envoi au backend en cours
    READY,          // Video generee disponible
    PUBLISHED,      // Publiee sur Facebook
    FAILED          // Erreur (a re-essayer)
}

/**
 * Format de la video finale.
 */
enum class AdFormat(val ratio: String) {
    SQUARE("1:1"),
    PORTRAIT("9:16"),
    LANDSCAPE("16:9")
}

/**
 * Modele de domaine d'un projet de pub. Persiste en Room (AdProjectEntity).
 *
 * Les chemins avatarPath/productPath/videoPath pointent vers
 * le stockage interne de l'app (filesDir) — pas vers des URIs externes.
 */
data class AdProject(
    val id: Long = 0L,
    val title: String,
    val avatarPath: String? = null,
    val productPath: String? = null,
    val script: String = "",
    val durationSeconds: Int = 30,
    val format: AdFormat = AdFormat.PORTRAIT,
    val voiceTone: String = "naturelle",
    val language: String = "fr-CA",
    val status: AdProjectStatus = AdProjectStatus.DRAFT,
    val videoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
