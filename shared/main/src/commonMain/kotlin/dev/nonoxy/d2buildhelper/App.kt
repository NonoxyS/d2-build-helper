package dev.nonoxy.d2buildhelper

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.ui.api.GuidesRoute
import dev.nonoxy.d2buildhelper.feature.guides.ui.api.composableGuidesScreen

@Composable
fun App() = D2BuildHelperTheme {
    D2BuildHelperApp()
}

@Composable
private fun D2BuildHelperApp(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = GuidesRoute,
    ) {
        composableGuidesScreen()
    }
}
