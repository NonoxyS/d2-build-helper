package com.nonoxy.d2buildhelper

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nonoxy.d2buildhelper.presentation.guides.GuidesScreen
import com.nonoxy.d2buildhelper.presentation.guides.GuidesScreenViewModel
import com.nonoxy.d2buildhelper.presentation.filterview.HeroFilterViewModel
import com.nonoxy.d2buildhelper.presentation.hero_guides.HeroGuidesScreen
import com.nonoxy.d2buildhelper.presentation.hero_guides.HeroGuidesScreenViewModel
import com.nonoxy.d2buildhelper.ui.theme.D2BuildHelperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            D2BuildHelperTheme {
                window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
                window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val viewModelGuides = hiltViewModel<GuidesScreenViewModel>()
                    val viewModelHeroGuides = hiltViewModel<HeroGuidesScreenViewModel>()
                    val viewModelHeroFilter = hiltViewModel<HeroFilterViewModel>()
                    val heroGuidesBuildState by viewModelHeroGuides.buildsState.collectAsState()
                    val guidesBuildsState by viewModelGuides.buildsState.collectAsState()
                    val heroFilterState by viewModelHeroFilter.heroFilterState.collectAsState()

                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "guides") {
                        composable(route = "guides") {
                            GuidesScreen(
                                navController = navController,
                                buildsState = guidesBuildsState,
                                heroFilterState = heroFilterState,
                                onSelectHero = viewModelHeroFilter::selectHero
                            )
                        }
                        composable(
                            route = "heroGuides/{heroId}",
                            arguments = listOf(navArgument("heroId") {
                                    type = NavType.IntType })) { backStackEntry ->
                            val heroId = backStackEntry.arguments?.getInt("heroId")?.toShort()
                            Log.d("HeroFilter", "in navigate")
                            LaunchedEffect(key1 = heroId) {
                                Log.d("HeroFilter", "LAUNCHED EFFECT: $heroId")
                                viewModelHeroGuides.getHeroGuidesData(
                                    heroId = heroId ?: 48)
                            }

                            Log.d("HeroFilter", "start copy data")
                            HeroGuidesScreen(
                                navController = navController,
                                buildsState = heroGuidesBuildState,
                                heroFilterState = heroFilterState,
                                onSelectHero = viewModelHeroFilter::selectHero,
                            )
                        }
                    }
                }
            }
        }
    }
}