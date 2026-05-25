package dev.nonoxy.d2buildhelper.core.domain.models

data class Hero(
    val id: HeroId,
    val shortName: String,
    val displayName: String,
    val iconUrl: ImageUrl,
)
