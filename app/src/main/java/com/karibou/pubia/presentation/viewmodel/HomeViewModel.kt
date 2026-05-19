package com.karibou.pubia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karibou.pubia.data.repository.AdProjectRepository
import com.karibou.pubia.domain.model.AdProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de l'ecran d'accueil — observe la liste des projets en Room.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AdProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<AdProject>> = repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun delete(project: AdProject) {
        viewModelScope.launch { repository.delete(project) }
    }
}
