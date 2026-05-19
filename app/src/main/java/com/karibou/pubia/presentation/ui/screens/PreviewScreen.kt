package com.karibou.pubia.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.karibou.pubia.presentation.viewmodel.PreviewViewModel

/**
 * Etape 6 — apercu de la video generee. ExoPlayer + bouton telecharger
 * (galerie via MediaStore) + bouton "Publier sur Facebook".
 */
@Composable
fun PreviewScreen(
    videoUrl: String,
    onPublish: () -> Unit,
    onDone: () -> Unit,
    viewModel: PreviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // ExoPlayer — cree au premier compose, libere au depart.
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    ScreenScaffold(title = "Etape 6 — Apercu", onBack = onDone) {
        Text(
            text = "Voici ta pub Karibou !",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f) // adapte au format portrait par defaut
        )

        when {
            state.isDownloading -> CircularProgressIndicator()
            state.downloadResultUri != null -> Text(
                text = "Sauvegardee dans la galerie !",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            else -> Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Arrangement.CenterHorizontally)
            ) {
                OutlinedButton(onClick = { viewModel.download(context, videoUrl) }) {
                    Text("Telecharger")
                }
                Button(onClick = onPublish) {
                    Text("Publier sur Facebook")
                }
            }
        }

        state.errorMessage?.let { err ->
            Text(
                text = err,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }

        OutlinedButton(onClick = onDone) {
            Text("Retour a l'accueil")
        }
    }
}
