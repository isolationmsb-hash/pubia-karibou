package com.karibou.pubia.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karibou.pubia.data.repository.AdProjectRepository
import com.karibou.pubia.domain.model.AdFormat
import com.karibou.pubia.domain.model.AdProject
import com.karibou.pubia.domain.model.AdProjectStatus
import com.karibou.pubia.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Etat du wizard de creation d'une pub. Partage entre tous les ecrans du
 * parcours (Avatar -> Produit -> Script -> Options -> Generation -> Preview).
 *
 * Le scoping est gere par hiltViewModel(navBackStackEntry) au niveau du
 * sous-graphe "wizard" dans NavGraph, ce qui assure qu'une seule instance
 * vit pour toute la duree de la creation d'un projet, puis est detruite
 * quand l'utilisateur revient a l'accueil.
 */
@HiltViewModel
class AdWizardViewModel @Inject constructor(
    private val repository: AdProjectRepository,
    private val compressor: ImageCompressor
) : ViewModel() {

    /** Etat UI du wizard — observe par les ecrans Compose. */
    data class WizardState(
        val project: AdProject = newDraftProject(),
        val isProcessing: Boolean = false,
        val errorMessage: String? = null
    )

    private val _state = MutableStateFlow(WizardState())
    val state: StateFlow<WizardState> = _state.asStateFlow()

    /**
     * Compresse l'image selectionnee et l'attache au projet (avatar ou produit).
     * @param category "avatar" ou "product" — sert aussi de sous-dossier de stockage.
     */
    fun onImagePicked(context: Context, uri: Uri, category: String) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, errorMessage = null) }
            try {
                val bytes = compressor.compress(context, uri)
                val path = repository.storeImage(category, bytes)
                _state.update { s ->
                    val updatedProject = when (category) {
                        "avatar" -> s.project.copy(avatarPath = path)
                        "product" -> s.project.copy(productPath = path)
                        else -> s.project
                    }
                    s.copy(project = updatedProject, isProcessing = false)
                }
            } catch (t: Throwable) {
                _state.update {
                    it.copy(isProcessing = false, errorMessage = "Echec compression : ${t.message}")
                }
            }
        }
    }

    fun onScriptChanged(text: String) {
        _state.update { it.copy(project = it.project.copy(script = text)) }
    }

    fun onDurationChanged(seconds: Int) {
        _state.update { it.copy(project = it.project.copy(durationSeconds = seconds)) }
    }

    fun onFormatChanged(format: AdFormat) {
        _state.update { it.copy(project = it.project.copy(format = format)) }
    }

    fun onTitleChanged(title: String) {
        _state.update { it.copy(project = it.project.copy(title = title)) }
    }

    /**
     * Persiste le brouillon en cours dans Room. Appele quand l'utilisateur
     * passe a l'etape Generation. Retourne l'id du projet sauvegarde.
     */
    suspend fun saveDraft(): Long {
        val project = _state.value.project.copy(status = AdProjectStatus.DRAFT)
        val id = if (project.id == 0L) {
            repository.create(project)
        } else {
            repository.update(project)
            project.id
        }
        _state.update { it.copy(project = it.project.copy(id = id)) }
        return id
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    companion object {
        private fun newDraftProject(): AdProject {
            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA_FRENCH).format(Date())
            return AdProject(title = "Pub Karibou — $ts")
        }
    }
}
