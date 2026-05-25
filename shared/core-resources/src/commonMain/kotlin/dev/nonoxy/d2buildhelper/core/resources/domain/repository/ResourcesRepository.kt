package dev.nonoxy.d2buildhelper.core.resources.domain.repository

import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants

interface ResourcesRepository {

    /**
     * Returns DotaConstants from cache (L1 in-memory → L2 DataStore → falls back to network if both empty).
     *
     * As a side effect, kicks off a background refresh (single-flight; if one is already in flight, does nothing).
     * The cache returned here may be stale by up to one refresh-cycle; use [refreshDotaConstants] when a freshness
     * requirement exists (e.g. version mismatch with guides response).
     */
    suspend fun getDotaConstants(): Result<DotaConstants>

    /**
     * Returns network-fresh DotaConstants. Joins an in-flight refresh if one is active (single-flight: only one
     * network call concurrently). If a refresh completed within the last minute AND [expectedVersion] is null OR
     * satisfied by the cached version, returns the cache without firing network.
     *
     * @param expectedVersion when non-null, bypasses the 1-minute throttle if cached gameVersion < expectedVersion.
     */
    suspend fun refreshDotaConstants(
        expectedVersion: GameVersion? = null,
    ): Result<DotaConstants>
}
