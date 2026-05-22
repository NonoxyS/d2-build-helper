package dev.nonoxy.d2buildhelper.core.navigation

import kotlinx.serialization.Serializable

interface Screen

@Serializable
data object GuidesRoute : Screen
