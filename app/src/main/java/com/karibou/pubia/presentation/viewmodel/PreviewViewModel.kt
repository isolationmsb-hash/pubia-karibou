package com.karibou.pubia.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karibou.pubia.util.VideoDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val downloader: VideoDownloader
) : ViewModel() {

    data class State(
        val isDownloading: Boolean = false,
        val downloadResultUri: String? = null,
        val errorMessage: String? = null
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun download(context: Context, videoUrl: String) {
        viewModelScope.launch {
            _state.update { it.copy(isDownloading = true, errorMessage = null) }
            downloader.downloadToGallery(context, videoUrl).fold(
                onSuccess = { uri ->
                    _state.update {
                        it.copy(isDownloading = false, downloadResultUri = uri)
                    }
                },
                onFailure = { t ->
                    _state.update {
                        it.copy(isDownloading = false, errorMessage = "Echec : ${t.message}")
                    }
                }
            )
        }
    }
}
