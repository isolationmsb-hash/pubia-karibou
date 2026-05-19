package com.karibou.pubia.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.karibou.pubia.domain.model.AdFormat
import com.karibou.pubia.presentation.viewmodel.AdWizardViewModel

/**
 * Etape 4 — duree + format. Voix et langue restent par defaut (fr-CA + ElevenLabs)
 * pour le MVP Karibou.
 */
@Composable
fun OptionsScreen(
    onNext: () -> Unit,
    onBack: () -> Unit,
    wizardEntry: NavBackStackEntry
) {
    val wizardViewModel: AdWizardViewModel = hiltViewModel(wizardEntry)
    val state by wizardViewModel.state.collectAsState()

    ScreenScaffold(title = "Etape 4 — Options", onBack = onBack) {
        Text(
            text = "Duree de la video",
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            listOf(15, 30, 60).forEach { sec ->
                FilterChip(
                    selected = state.project.durationSeconds == sec,
                    onClick = { wizardViewModel.onDurationChanged(sec) },
                    label = { Text("${sec}s") }
                )
            }
        }

        Text(
            text = "Format",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            formatOptions.forEach { (format, label) ->
                FilterChip(
                    selected = state.project.format == format,
                    onClick = { wizardViewModel.onFormatChanged(format) },
                    label = { Text(label) }
                )
            }
        }

        Text(
            text = "Voix et langue : francais quebecois (ElevenLabs).",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )

        Button(onClick = onNext) {
            Text("Lancer la generation")
        }
    }
}

private val formatOptions = listOf(
    AdFormat.PORTRAIT to "9:16 (Reels)",
    AdFormat.SQUARE to "1:1 (Fil)",
    AdFormat.LANDSCAPE to "16:9 (Bureau)"
)
