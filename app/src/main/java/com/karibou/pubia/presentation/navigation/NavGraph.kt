package com.karibou.pubia.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.karibou.pubia.presentation.ui.screens.AvatarScreen
import com.karibou.pubia.presentation.ui.screens.GenerationScreen
import com.karibou.pubia.presentation.ui.screens.HomeScreen
import com.karibou.pubia.presentation.ui.screens.OptionsScreen
import com.karibou.pubia.presentation.ui.screens.PreviewScreen
import com.karibou.pubia.presentation.ui.screens.ProductScreen
import com.karibou.pubia.presentation.ui.screens.PublishFBScreen
import com.karibou.pubia.presentation.ui.screens.ScriptScreen

private const val WIZARD_GRAPH = "wizard"
private const val ARG_VIDEO_URL = "videoUrl"

@Composable
fun PubIANavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.path
    ) {
        composable(Route.Home.path) {
            HomeScreen(onNewProject = { navController.navigate(WIZARD_GRAPH) })
        }
        wizardGraph(navController)
    }
}

private fun NavGraphBuilder.wizardGraph(navController: NavController) {
    navigation(startDestination = Route.Avatar.path, route = WIZARD_GRAPH) {

        composable(Route.Avatar.path) { entry ->
            val wizardEntry = remember(entry) { navController.getBackStackEntry(WIZARD_GRAPH) }
            AvatarScreen(
                onNext = { navController.navigate(Route.Product.path) },
                onBack = { navController.popBackStack() },
                wizardEntry = wizardEntry
            )
        }

        composable(Route.Product.path) { entry ->
            val wizardEntry = remember(entry) { navController.getBackStackEntry(WIZARD_GRAPH) }
            ProductScreen(
                onNext = { navController.navigate(Route.Script.path) },
                onBack = { navController.popBackStack() },
                wizardEntry = wizardEntry
            )
        }

        composable(Route.Script.path) { entry ->
            val wizardEntry = remember(entry) { navController.getBackStackEntry(WIZARD_GRAPH) }
            ScriptScreen(
                onNext = { navController.navigate(Route.Options.path) },
                onBack = { navController.popBackStack() },
                wizardEntry = wizardEntry
            )
        }

        composable(Route.Options.path) { entry ->
            val wizardEntry = remember(entry) { navController.getBackStackEntry(WIZARD_GRAPH) }
            OptionsScreen(
                onNext = { navController.navigate(Route.Generation.path) },
                onBack = { navController.popBackStack() },
                wizardEntry = wizardEntry
            )
        }

        composable(Route.Generation.path) { entry ->
            val wizardEntry = remember(entry) { navController.getBackStackEntry(WIZARD_GRAPH) }
            GenerationScreen(
                onComplete = { videoUrl ->
                    navController.navigate("${Route.Preview.path}/${Uri.encode(videoUrl)}") {
                        popUpTo(Route.Generation.path) { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack(Route.Home.path, inclusive = false) },
                wizardEntry = wizardEntry
            )
        }

        composable(
            route = "${Route.Preview.path}/{$ARG_VIDEO_URL}",
            arguments = listOf(navArgument(ARG_VIDEO_URL) { type = NavType.StringType })
        ) { entry ->
            val videoUrl = Uri.decode(entry.arguments?.getString(ARG_VIDEO_URL) ?: "")
            PreviewScreen(
                videoUrl = videoUrl,
                onPublish = { navController.navigate(Route.PublishFB.path) },
                onDone = { navController.popBackStack(Route.Home.path, inclusive = false) }
            )
        }

        composable(Route.PublishFB.path) {
            PublishFBScreen(
                onDone = { navController.popBackStack(Route.Home.path, inclusive = false) }
            )
        }
    }
}
