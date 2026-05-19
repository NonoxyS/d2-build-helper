package dev.nonoxy.d2buildhelper.feature.guides.impl.data.repository

import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide

interface GuidesRepository {
    suspend fun getGuides(): Result<List<Guide>>

    suspend fun getHeroGuides(heroId: Short): Result<List<Guide>>
}
