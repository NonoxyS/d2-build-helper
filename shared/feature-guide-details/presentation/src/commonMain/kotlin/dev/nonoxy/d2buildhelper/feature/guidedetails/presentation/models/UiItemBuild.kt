package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

/**
 * Card 3 — "Билд предметов".
 *
 * Purchases are grouped into game-phase [sections] (Laning / Mid / Late).
 * Phases with zero purchases are omitted. The [neutralItem] is rendered separately.
 */
data class UiItemBuild(
    val neutralItem: UiItemBuildEntry?,
    val sections: ImmutableList<UiItemBuildSection>,
)

data class UiItemBuildSection(
    val phase: UiItemBuildPhase,
    val entries: ImmutableList<UiItemBuildEntry>,
)

/**
 * Game phase for item purchase grouping.
 *
 * Note: the phase → display string mapping lives in the UI layer ([ItemBuildCard])
 * because [dev.icerock.moko.resources.StringResource] cannot be referenced here —
 * moko's `MR.strings` initialiser requires Android resources on the classpath, which
 * are absent in host-JVM (commonTest) runs.
 */
enum class UiItemBuildPhase {
    LANING,
    MID_GAME,
    LATE_GAME,
}

data class UiItemBuildEntry(
    val iconUrl: ImageUrl?,
    /** Item display name from constants (popup title); `null` when unknown. */
    val name: String?,
    val timeText: String,
)
