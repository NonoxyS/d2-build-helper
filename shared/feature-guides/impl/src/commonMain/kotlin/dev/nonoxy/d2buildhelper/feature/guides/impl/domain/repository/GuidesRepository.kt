package dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository

import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide

internal interface GuidesRepository {

    suspend fun getGuides(): Result<List<Guide>>

    suspend fun getHeroGuides(heroId: Short): Result<List<Guide>>
}
