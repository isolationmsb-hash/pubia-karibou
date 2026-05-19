package com.karibou.pubia.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.karibou.pubia.domain.model.AdProject
import com.karibou.pubia.domain.model.AdProjectStatus
import com.karibou.pubia.presentation.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Accueil — liste des projets persistes en Room. FAB pour creer une nouvelle pub.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNewProject: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PubIA Karibou") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewProject,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nouvelle pub") }
            )
        }
    ) { padding ->
        if (projects.isEmpty()) {
            EmptyState(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(projects, key = { it.id }) { project ->
                    ProjectCard(project)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "Aucune pub creee pour le moment",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Touche 'Nouvelle pub' pour generer ta premiere publicite Karibou avec l'IA.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProjectCard(project: AdProject) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${statusLabel(project.status)} — ${formatDate(project.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun statusLabel(s: AdProjectStatus): String = when (s) {
    AdProjectStatus.DRAFT -> "Brouillon"
    AdProjectStatus.GENERATING -> "Generation..."
    AdProjectStatus.READY -> "Pret"
    AdProjectStatus.PUBLISHED -> "Publie"
    AdProjectStatus.FAILED -> "Echec"
}

private fun formatDate(ts: Long): String =
    SimpleDateFormat("d MMM yyyy HH:mm", Locale.CANADA_FRENCH).format(Date(ts))
