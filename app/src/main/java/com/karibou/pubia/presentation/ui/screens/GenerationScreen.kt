package com.karibou.pubia.presentation.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.karibou.pubia.presentation.viewmodel.AdWizardViewModel
import com.karibou.pubia.presentation.viewmodel.GenerationViewModel

/**
 * Etape 5 — generation. Demarre l'appel backend au premier composition,
 * affiche progression + statut, navigue vers Preview quand termine.
 */
@Composable
fun GenerationScreen(
    onComplete: (videoUrl: String) -> Unit,
    onCancel: () -> Unit,
    wizardEntry: NavBackStackEntry,
    generationViewModel: GenerationViewModel = hiltViewModel()
) {
    val wizardViewModel: AdWizardViewModel = hiltViewModel(wizardEntry)
    val wizardState by wizardViewModel.state.collectAsState()
    val state by generationViewModel.state.collectAsState()

    // Demarre la generation au premier passage seulement
    LaunchedEffect(Unit) {
        if (state.jobId == null && !state.isStarting) {
            // Persiste le brouillon avant l'envoi
            val savedId = wizardViewModel.saveDraft()
            val projectToSend = wizardState.project.copy(id = savedId)
            generationViewModel.start(projectToSend)
        }
    }

    // Navigation automatique vers Preview quand pret
    LaunchedEffect(state.isFinished, state.videoUrl) {
        if (state.isFinished && state.videoUrl != null) {
            onComplete(state.videoUrl!!)
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = state.progress / 100f,
        label = "progress"
    )

    ScreenScaffold(title = "Etape 5 — Generation") {
        Text(
            text = statusLabel(state.backendStatus),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )

        Text(
            text = "${state.progress}%",
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = "La generation prend habituellement 1 a 3 minutes. Tu peux laisser l'app ouverte.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        state.errorMessage?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }

        TextButton(onClick = {
            generationViewModel.cancel()
            onCancel()
        }) {
            Text("Annuler")
        }
    }
}

private fun statusLabel(status: String): String = when (status) {
    "queued" -> "En file d'attente..."
    "generating_voice" -> "Generation de la voix..."
    "generating_scene" -> "Generation de la scene..."
    "generating_avatar" -> "Generation de l'avatar parlant..."
    "assembling" -> "Assemblage final..."
    "ready" -> "Termine !"
    "failed" -> "Echec"
    else -> status
}
