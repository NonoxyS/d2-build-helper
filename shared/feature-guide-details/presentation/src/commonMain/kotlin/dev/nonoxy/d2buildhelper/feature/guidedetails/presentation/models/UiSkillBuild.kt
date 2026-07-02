package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

data class UiSkillBuild(
    val summary: UiSkillSummary,
    val matrix: UiSkillMatrix,
    val talents: ImmutableList<UiTalent>,
)

data class UiSkillSummary(
    val talentTierTaken: ImmutableList<Boolean>,
    val abilities: ImmutableList<UiAbilitySummary>,
    val scepterPurchased: Boolean,
)

data class UiAbilitySummary(
    val iconUrl: ImageUrl?,
    val name: String?,
    val pointCount: Int,
    val earlyPointCount: Int,
    val isUltimate: Boolean,
)

data class UiSkillMatrix(
    val rows: ImmutableList<UiSkillMatrixRow>,
) {
    companion object {
        const val LEVEL_COLUMNS = 30
    }
}

data class UiSkillMatrixRow(
    val iconUrl: ImageUrl?,
    val name: String?,
    val isStat: Boolean,
    val isUltimate: Boolean,
    val marks: ImmutableList<Boolean>,
)
