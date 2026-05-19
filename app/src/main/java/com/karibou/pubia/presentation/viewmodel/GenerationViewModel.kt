package com.karibou.pubia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karibou.pubia.data.repository.AdProjectRepository
import com.karibou.pubia.domain.model.AdProject
import com.karibou.pubia.domain.model.AdProjectStatus
import com.karibou.pubia.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de l'ecran de generation. Lance l'appel backend puis fait
 * du polling sur /api/status/:jobId toutes les 5s jusqu'a ready ou failed.
 */
@HiltViewModel
class GenerationViewModel @Inject constructor(
    private val repository: AdProjectRepository
) : ViewModel() {

    data class State(
        val isStarting: Boolean = false,
        val jobId: String? = null,
        val backendStatus: String = "queued",
        val progress: Int = 0,
        val videoUrl: String? = null,
        val errorMessage: String? = null,
        val isFinished: Boolean = false
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private var pollingJob: Job? = null

    fun start(project: AdProject) {
        if (_state.value.isStarting || _state.value.jobId != null) return
        viewModelScope.launch {
            _state.update { it.copy(isStarting = true, errorMessage = null) }
            try {
                val started = repository.startGeneration(project)
                _state.update {
                    it.copy(isStarting = false, jobId = started.jobId, backendStatus = started.status)
                }
                // Met a jour Room — projet maintenant en GENERATING
                repository.update(project.copy(status = AdProjectStatus.GENERATING))
                startPolling(project, started.jobId)
            } catch (t: Throwable) {
                _state.update {
                    it.copy(isStarting = false, errorMessage = "Echec demarrage : ${t.message}")
                }
            }
        }
    }

    private fun startPolling(project: AdProject, jobId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            val start = System.currentTimeMillis()
            while (true) {
                if (System.currentTimeMillis() - start > Constants.GENERATION_TIMEOUT_MS) {
                    _state.update {
                        it.copy(isFinished = true, errorMessage = "Timeout (10 min) — verifie le backend")
                    }
                    return@launch
                }
                try {
                    val status = repository.pollStatus(jobId)
                    _state.update {
                        it.copy(
                            backendStatus = status.status,
                            progress = status.progress,
                            videoUrl = status.videoUrl,
                            errorMessage = status.error
                        )
                    }
                    if (status.isFinished) {
                        val newStatus = if (status.isSuccess) AdProjectStatus.READY else AdProjectStatus.FAILED
                        repository.update(project.copy(
                            status = newStatus,
                            videoPath = status.videoUrl
                        ))
                        _state.update { it.copy(isFinished = true) }
                        return@launch
                    }
                } catch (t: Throwable) {
                    // Backend possiblement endormi — on continue le polling
                    _state.update { it.copy(errorMessage = "Connexion : ${t.message}") }
                }
                delay(Constants.GENERATION_POLL_INTERVAL_MS)
            }
        }
    }

    fun cancel() {
        pollingJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
