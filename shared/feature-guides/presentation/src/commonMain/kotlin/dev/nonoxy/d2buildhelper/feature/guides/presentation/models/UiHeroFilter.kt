package dev.nonoxy.d2buildhelper.feature.guides.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl

data class UiHeroFilter(
    val heroId: HeroId,
    val displayName: String,
    val iconUrl: ImageUrl,
)
