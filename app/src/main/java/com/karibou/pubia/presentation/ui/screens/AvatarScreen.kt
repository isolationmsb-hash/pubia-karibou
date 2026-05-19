package com.karibou.pubia.presentation.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.karibou.pubia.presentation.viewmodel.AdWizardViewModel
import java.io.File

/**
 * Etape 1 — selection de la photo avatar via Photo Picker (API moderne,
 * ne necessite aucune permission).
 */
@Composable
fun AvatarScreen(
    onNext: () -> Unit,
    onBack: () -> Unit,
    wizardEntry: NavBackStackEntry
) {
    val wizardViewModel: AdWizardViewModel = hiltViewModel(wizardEntry)
    val state by wizardViewModel.state.collectAsState()
    val context = LocalContext.current

    val pickPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            wizardViewModel.onImagePicked(context, uri, category = "avatar")
        }
    }

    ScreenScaffold(title = "Etape 1 — Avatar", onBack = onBack) {
        Text(
            text = "Choisis une photo claire de la personne qui apparaitra dans la pub.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isProcessing -> {
                        CircularProgressIndicator()
                    }
                    state.project.avatarPath != null -> {
                        AsyncImage(
                            model = File(state.project.avatarPath!!),
                            contentDescription = "Avatar selectionne",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                pickPhoto.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            enabled = !state.isProcessing
        ) {
            Text(
                if (state.project.avatarPath == null) "Choisir une photo"
                else "Changer la photo"
            )
        }

        state.errorMessage?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onNext,
            enabled = state.project.avatarPath != null && !state.isProcessing
        ) {
            Text("Suivant : produit")
        }
    }
}
