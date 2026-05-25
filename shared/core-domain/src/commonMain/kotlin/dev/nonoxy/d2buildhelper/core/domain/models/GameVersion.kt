package dev.nonoxy.d2buildhelper.core.domain.models

import kotlin.jvm.JvmInline

/**
 * Stratz-assigned monotonically-increasing game version identifier
 * (mirrors `gameVersionId: Int` from `/v1/constants` and `/v1/guides`).
 */
@JvmInline
value class GameVersion(val id: Int) : Comparable<GameVersion> {
    override fun compareTo(other: GameVersion): Int = id.compareTo(other.id)
}
