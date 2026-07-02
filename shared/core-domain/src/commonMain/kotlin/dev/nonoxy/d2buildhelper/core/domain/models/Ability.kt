package dev.nonoxy.d2buildhelper.core.domain.models

data class Ability(
    val id: AbilityId,
    val name: String,
    val displayName: String,
    val iconUrl: ImageUrl,
)
