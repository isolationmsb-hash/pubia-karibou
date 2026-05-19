package com.karibou.pubia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.karibou.pubia.domain.model.AdFormat
import com.karibou.pubia.domain.model.AdProject
import com.karibou.pubia.domain.model.AdProjectStatus

/**
 * Entite Room pour AdProject. Le format/status sont stockes en TEXT
 * (nom de l'enum) — gere via TypeConverters dans PubIADatabase.
 */
@Entity(tableName = "ad_projects")
data class AdProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val avatarPath: String?,
    val productPath: String?,
    val script: String,
    val durationSeconds: Int,
    val format: AdFormat,
    val voiceTone: String,
    val language: String,
    val status: AdProjectStatus,
    val videoPath: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): AdProject = AdProject(
        id = id,
        title = title,
        avatarPath = avatarPath,
        productPath = productPath,
        script = script,
        durationSeconds = durationSeconds,
        format = format,
        voiceTone = voiceTone,
        language = language,
        status = status,
        videoPath = videoPath,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(p: AdProject): AdProjectEntity = AdProjectEntity(
            id = p.id,
            title = p.title,
            avatarPath = p.avatarPath,
            productPath = p.productPath,
            script = p.script,
            durationSeconds = p.durationSeconds,
            format = p.format,
            voiceTone = p.voiceTone,
            language = p.language,
            status = p.status,
            videoPath = p.videoPath,
            createdAt = p.createdAt,
            updatedAt = p.updatedAt
        )
    }
}
