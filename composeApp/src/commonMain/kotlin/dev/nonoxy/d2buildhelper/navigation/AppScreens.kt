package dev.nonoxy.d2buildhelper.navigation

sealed class AppScreens(val route: String) {
    data object Guides : AppScreens(route = "guides")
    data object HeroGuides : AppScreens(route = "heroGuides/{heroId}") {
        fun passHeroId(heroId: Short) = "heroGuides/$heroId"
    }
    data object DetailGuide : AppScreens(route = "detailGuide")
}