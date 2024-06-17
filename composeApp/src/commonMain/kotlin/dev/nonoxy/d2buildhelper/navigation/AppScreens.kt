package dev.nonoxy.d2buildhelper.navigation

import kotlinx.serialization.Serializable

enum class AppScreens(val title: String) {
    Guides("guides"), HeroGuides("heroGuides"), DetailGuide("detailGuide")
}