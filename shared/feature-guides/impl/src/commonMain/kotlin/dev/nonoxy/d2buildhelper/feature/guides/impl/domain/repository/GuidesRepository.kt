package dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository

import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesPage

internal interface GuidesRepository {

    suspend fun getGuides(): Result<GuidesPage>

    suspend fun getHeroGuides(heroId: HeroId): Result<GuidesPage>
}
