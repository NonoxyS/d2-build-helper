package dev.nonoxy.d2buildhelper.core.resources.data.storage

import dev.nonoxy.d2buildhelper.core.resources.domain.models.CachedDotaConstants

interface ConstantsStorage {

    suspend fun load(): CachedDotaConstants?

    suspend fun save(value: CachedDotaConstants)
}
