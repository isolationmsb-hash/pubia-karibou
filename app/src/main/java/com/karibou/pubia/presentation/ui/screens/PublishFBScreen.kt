package com.karibou.pubia.presentation.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Etape 7 — publication Facebook. PHASE 6 — Meta Marketing API.
 *
 * Pour le MVP, on affiche :
 *  - le consentement legal IA obligatoire (regles Meta 2024+)
 *  - un placeholder "OAuth Meta" pour la connexion (a coder en Phase 6 complete)
 *  - un fallback "Partager manuellement" qui ouvre la galerie pour upload manuel
 */
@Composable
fun PublishFBScreen(onDone: () -> Unit) {
    var consentImage by remember { mutableStateOf(false) }
    var consentAiMention by remember { mutableStateOf(false) }

    ScreenScaffold(title = "Etape 7 — Publication Facebook", onBack = onDone) {

        Text(
            text = "Conformite legale",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        ConsentCard(
            text = "Je certifie avoir le droit d'utiliser l'image de la personne dans cette pub (moi-meme, ou consentement ecrit signe).",
            checked = consentImage,
            onCheckedChange = { consentImage = it }
        )

        ConsentCard(
            text = "Je comprends que la mention \"Contenu genere par IA\" sera ajoutee automatiquement a la publication (obligatoire selon les regles Meta 2024+).",
            checked = consentAiMention,
            onCheckedChange = { consentAiMention = it }
        )

        val canPublish = consentImage && consentAiMention

        Text(
            text = "Connexion Meta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(onClick = { /* TODO Phase 6 : OAuth Meta */ }, enabled = canPublish) {
            Text("Se connecter avec Facebook")
        }

        Text(
            text = "Phase 6 — OAuth Meta + Marketing API a venir. Pour l'instant, telecharge la video et publie manuellement.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )

        OutlinedButton(onClick = onDone) {
            Text("Retour a l'accueil")
        }
    }
}

@Composable
private fun ConsentCard(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
