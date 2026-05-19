package com.karibou.pubia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.karibou.pubia.presentation.navigation.PubIANavGraph
import com.karibou.pubia.presentation.theme.PubIAKaribouTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity unique — heberge tout le NavHost Compose.
 * @AndroidEntryPoint permet d'injecter des dependances Hilt dans les Composables.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PubIAKaribouTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PubIANavGraph()
                }
            }
        }
    }
}
