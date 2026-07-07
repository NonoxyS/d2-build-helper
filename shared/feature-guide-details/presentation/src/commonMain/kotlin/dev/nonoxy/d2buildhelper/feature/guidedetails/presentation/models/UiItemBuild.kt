package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

data class UiItemBuild(
    val neutralItem: UiItemBuildEntry?,
    val sections: ImmutableList<UiItemBuildSection>,
)

data class UiItemBuildSection(
    val phase: UiItemBuildPhase,
    val entries: ImmutableList<UiItemBuildEntry>,
)

// Phase → display string lives in UI layer: MR.strings requires Android classpath absent in commonTest
enum class UiItemBuildPhase {
    LANING,
    MID_GAME,
    LATE_GAME,
}

data class UiItemBuildEntry(
    val iconUrl: ImageUrl?,
    val name: String?,
    val timeText: String,
    val count: Int = 1,
)
