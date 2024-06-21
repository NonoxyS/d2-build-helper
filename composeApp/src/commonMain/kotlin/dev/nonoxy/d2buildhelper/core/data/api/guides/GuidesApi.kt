package dev.nonoxy.d2buildhelper.core.data.api.guides

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import kotlinx.coroutines.flow.Flow

interface GuidesApi {
    fun getGuides(): Flow<RequestResult<List<Guide>>>
}