package dev.nonoxy.d2buildhelper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import dev.nonoxy.d2buildhelper.base.LocalImageLoader
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import dev.nonoxy.d2buildhelper.features.detailGuide.DetailGuideScreen
import dev.nonoxy.d2buildhelper.features.guides.presentation.ui.GuidesScreen
import dev.nonoxy.d2buildhelper.features.heroGuides.HeroGuidesScreen
import dev.nonoxy.d2buildhelper.navigation.AppScreens
import dev.nonoxy.d2buildhelper.navigation.LocalNavHost
import dev.nonoxy.d2buildhelper.theme.D2BuildHelperTheme

@Composable
internal fun App() = D2BuildHelperTheme {
    InjectProvider.addDependency(
        type = ImageLoader::class,
        dependency = InjectProvider.createCoinImageLoader(LocalPlatformContext.current)
    )
    D2BuildHelperApp()
}

@Composable
internal fun D2BuildHelperApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: AppScreens.Guides

    CompositionLocalProvider(
        LocalNavHost provides navController,
        LocalImageLoader provides InjectProvider.getDependency(ImageLoader::class)
    ) {
        NavHost(
            navController = navController,
            startDestination = AppScreens.Guides.title
        ) {
            composable(route = AppScreens.Guides.title) {
                GuidesScreen()
            }

            composable(route = AppScreens.HeroGuides.title) {
                HeroGuidesScreen()
            }

            composable(route = AppScreens.DetailGuide.title) {
                DetailGuideScreen()
            }
        }
    }
}

internal expect fun openUrl(url: String?)