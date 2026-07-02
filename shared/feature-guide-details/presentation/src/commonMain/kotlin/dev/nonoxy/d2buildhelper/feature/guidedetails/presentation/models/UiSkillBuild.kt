package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

data class UiSkillBuild(
    val summary: UiSkillSummary,
    val matrix: UiSkillMatrix,
)

data class UiSkillSummary(
    val talentTiers: ImmutableList<UiTalentTier>,
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
    val abilityRows: ImmutableList<UiSkillMatrixRow>,
    val bottomCells: ImmutableList<UiSkillMatrixBottomCell>,
) {
    companion object {
        const val LEVEL_COLUMNS = 25
    }
}

data class UiSkillMatrixRow(
    val iconUrl: ImageUrl?,
    val name: String?,
    val isUltimate: Boolean,
    val marks: ImmutableList<Boolean>,
)

sealed interface UiSkillMatrixBottomCell {
    data object Empty : UiSkillMatrixBottomCell
    data object Stat : UiSkillMatrixBottomCell
    data class Talent(val tier: Int, val side: TalentSide?, val text: String) : UiSkillMatrixBottomCell
}
