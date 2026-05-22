package dev.nonoxy.d2buildhelper.feature.guides.ui.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.nonoxy.d2buildhelper.core.navigation.GuidesRoute
import dev.nonoxy.d2buildhelper.feature.guides.ui.GuidesScreen

fun NavGraphBuilder.composableGuidesScreen() {
    composable<GuidesRoute> {
        GuidesScreen()
    }
}
