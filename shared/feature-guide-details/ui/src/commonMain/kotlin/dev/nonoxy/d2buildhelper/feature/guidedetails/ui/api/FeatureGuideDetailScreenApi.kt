package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.nonoxy.d2buildhelper.core.navigation.Screen
import dev.nonoxy.d2buildhelper.core.navigation.navigateOnResumed
import dev.nonoxy.d2buildhelper.core.navigation.popBackStackOnResumed
import dev.nonoxy.d2buildhelper.feature.guidedetails.ui.GuideDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class GuideDetailRoute(
    val matchId: Long,
    val steamAccountId: Long,
) : Screen

fun NavController.navigateToGuideDetailScreen(
    matchId: Long,
    steamAccountId: Long,
) {
    navigateOnResumed(GuideDetailRoute(matchId = matchId, steamAccountId = steamAccountId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.composableGuideDetailScreen(
    navController: NavController,
) {
    composable<GuideDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<GuideDetailRoute>()
        GuideDetailScreen(
            matchId = route.matchId,
            steamAccountId = route.steamAccountId,
            onBack = navController::popBackStackOnResumed,
        )
    }
}
