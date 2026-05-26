package dev.nonoxy.d2buildhelper.feature.guides.ui.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.nonoxy.d2buildhelper.core.navigation.Screen
import dev.nonoxy.d2buildhelper.core.navigation.navigateOnResumed
import dev.nonoxy.d2buildhelper.feature.guides.ui.GuidesScreen
import kotlinx.serialization.Serializable

@Serializable
data object GuidesRoute : Screen

fun NavController.navigateToGuidesScreen(
    popUpInclusive: Boolean = true,
    popUpToScreen: Screen? = null,
) {
    navigateOnResumed(GuidesRoute) {
        launchSingleTop = true
        popUpToScreen?.let { screen ->
            popUpTo(screen) { inclusive = popUpInclusive }
        }
    }
}

fun NavGraphBuilder.composableGuidesScreen() {
    composable<GuidesRoute> {
        GuidesScreen()
    }
}
