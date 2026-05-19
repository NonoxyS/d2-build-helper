package dev.nonoxy.d2buildhelper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.crossfade
import dev.nonoxy.d2buildhelper.common.ui.LocalImageLoader
import dev.nonoxy.d2buildhelper.features.detailGuide.DetailGuideScreen
import dev.nonoxy.d2buildhelper.feature.guides.ui.GuidesScreen
import dev.nonoxy.d2buildhelper.core.navigation.AppScreens
import dev.nonoxy.d2buildhelper.core.navigation.LocalNavHost
import dev.nonoxy.d2buildhelper.common.ui.theme.D2BuildHelperTheme

@Composable
fun App() = D2BuildHelperTheme {
    val platformContext = LocalPlatformContext.current
    val imageLoader = remember(platformContext) {
        ImageLoader.Builder(platformContext)
            .crossfade(true)
            .build()
    }

    CompositionLocalProvider(LocalImageLoader provides imageLoader) {
        D2BuildHelperApp()
    }
}

@Composable
fun D2BuildHelperApp(
    navController: NavHostController = rememberNavController()
) {
    CompositionLocalProvider(LocalNavHost provides navController) {
        NavHost(
            navController = navController,
            startDestination = AppScreens.Guides.route
        ) {
            composable(route = AppScreens.Guides.route) {
                GuidesScreen()
            }
            composable(route = AppScreens.DetailGuide.route) {
                DetailGuideScreen()
            }
        }
    }
}
