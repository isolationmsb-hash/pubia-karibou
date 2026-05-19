package com.karibou.pubia.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.karibou.pubia.data.repository.AdProjectRepository
import com.karibou.pubia.presentation.viewmodel.AdWizardViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Etape 3 — script. Champ texte multi-lignes + bouton "Ameliorer avec l'IA"
 * qui envoie le texte brut au backend (route /api/script/improve via Claude).
 */
@Composable
fun ScriptScreen(
    onNext: () -> Unit,
    onBack: () -> Unit,
    wizardEntry: NavBackStackEntry,
    helper: ScriptHelperViewModel = hiltViewModel()
) {
    val wizardViewModel: AdWizardViewModel = hiltViewModel(wizardEntry)
    val state by wizardViewModel.state.collectAsState()
    val helperState by helper.state.collectAsState()
    val scope = rememberCoroutineScope()

    var draft by remember { mutableStateOf(state.project.script) }

    ScreenScaffold(title = "Etape 3 — Script", onBack = onBack) {
        Text(
            text = "Decris ce que la personne fait et dit dans la pub.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = draft,
            onValueChange = {
                draft = it
                wizardViewModel.onScriptChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            placeholder = { Text("Ex: Je tiens la bougie, je respire profondement, et je dis a quel point elle parfume mon salon...") },
            minLines = 6,
            maxLines = 10
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            OutlinedButton(
                onClick = {
                    scope.launch {
                        helper.improve(draft, state.project.durationSeconds).let { improved ->
                            if (improved != null) {
                                draft = improved
                                wizardViewModel.onScriptChanged(improved)
                            }
                        }
                    }
                },
                enabled = draft.length >= 5 && !helperState.isLoading
            ) {
                if (helperState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp).width(20.dp))
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
                Text("Ameliorer avec l'IA")
            }
        }

        helperState.errorMessage?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onNext,
            enabled = draft.trim().length >= 5
        ) {
            Text("Suivant : options")
        }
    }
}

/**
 * Petit ViewModel local pour l'amelioration de script — separe du wizard
 * pour ne pas surcharger AdWizardViewModel.
 */
@HiltViewModel
class ScriptHelperViewModel @Inject constructor(
    private val repository: AdProjectRepository
) : androidx.lifecycle.ViewModel() {

    data class State(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _state = kotlinx.coroutines.flow.MutableStateFlow(State())
    val state: kotlinx.coroutines.flow.StateFlow<State> = _state

    suspend fun improve(raw: String, durationSeconds: Int): String? {
        if (raw.length < 5) return null
        _state.value = State(isLoading = true)
        return try {
            val improved = repository.improveScript(raw, durationSeconds)
            _state.value = State(isLoading = false)
            improved
        } catch (t: Throwable) {
            _state.value = State(isLoading = false, errorMessage = "Echec Claude : ${t.message}")
            null
        }
    }
}
