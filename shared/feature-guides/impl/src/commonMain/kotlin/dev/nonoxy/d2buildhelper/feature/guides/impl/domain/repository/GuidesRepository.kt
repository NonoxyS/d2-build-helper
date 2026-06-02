package dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository

import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilters
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesPage

internal interface GuidesRepository {

    suspend fun getGuides(filters: GuidesFilters, page: Int): Result<GuidesPage>
}
