package com.karibou.pubia.data.remote.dto

import com.squareup.moshi.JsonClass

/**
 * Reponse a POST /api/generate-ad.
 */
@JsonClass(generateAdapter = true)
data class GenerateAdResponse(
    val jobId: String,
    val status: String
)

/**
 * Reponse a GET /api/status/:jobId.
 * status enum cote backend : queued, generating_voice, generating_avatar, ready, failed
 */
@JsonClass(generateAdapter = true)
data class JobStatusResponse(
    val jobId: String,
    val status: String,
    val progress: Int,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val error: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    val isFinished: Boolean get() = status == "ready" || status == "failed"
    val isSuccess: Boolean get() = status == "ready"
}

@JsonClass(generateAdapter = true)
data class ImproveScriptRequest(
    val rawScript: String,
    val durationSeconds: Int
)

@JsonClass(generateAdapter = true)
data class ImproveScriptResponse(
    val script: String
)

@JsonClass(generateAdapter = true)
data class HealthResponse(
    val status: String,
    val timestamp: String
)
