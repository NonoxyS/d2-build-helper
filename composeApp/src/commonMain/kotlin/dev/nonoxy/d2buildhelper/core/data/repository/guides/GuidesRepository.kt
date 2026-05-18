package dev.nonoxy.d2buildhelper.core.data.repository.guides

import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide

interface GuidesRepository {
    suspend fun getGuides(): Result<List<Guide>>

    suspend fun getHeroGuides(heroId: Short): Result<List<Guide>>
}
