package dev.nonoxy.d2buildhelper.core.resources.domain.models

import kotlin.time.Instant

data class CachedDotaConstants(
    val data: DotaConstants,
    val fetchedAt: Instant,
)
