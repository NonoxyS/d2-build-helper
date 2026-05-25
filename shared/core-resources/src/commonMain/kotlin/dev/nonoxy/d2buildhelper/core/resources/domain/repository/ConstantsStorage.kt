package dev.nonoxy.d2buildhelper.core.resources.domain.repository

import dev.nonoxy.d2buildhelper.core.resources.domain.models.CachedDotaConstants

interface ConstantsStorage {

    /** Returns the cached [CachedDotaConstants] or `null` if nothing is persisted (or deserialization failed). */
    suspend fun load(): CachedDotaConstants?

    /** Persists [value] atomically, replacing any prior content. */
    suspend fun save(value: CachedDotaConstants)
}
