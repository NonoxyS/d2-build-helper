package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl
import kotlinx.collections.immutable.ImmutableList

/**
 * Card 2 — "Сборка способностей".
 *
 * [summary] is the top row (talent-tree petals + 4 ability point counters + scepter chip);
 * [matrix] is the level 1..[UiSkillMatrix.LEVEL_COLUMNS] grid with exactly one mark per column;
 * [talents] is the separate 10/15/20/25 list rendered under the matrix.
 */
data class UiSkillBuild(
    val summary: UiSkillSummary,
    val matrix: UiSkillMatrix,
    val talents: ImmutableList<UiTalent>,
)

data class UiSkillSummary(
    /** Which of the four talent tiers (10/15/20/25) have a talent taken, in tier order. */
    val talentTierTaken: ImmutableList<Boolean>,
    /** Q/W/E/R ability summaries with point counts, in learn order. */
    val abilities: ImmutableList<UiAbilitySummary>,
    val scepterPurchased: Boolean,
)

data class UiAbilitySummary(
    val iconUrl: ImageUrl?,
    val pointCount: Int,
)

/**
 * Skill matrix: [rows] × [LEVEL_COLUMNS] level columns.
 * Each column carries at most one mark across all rows (one point spent per level).
 */
data class UiSkillMatrix(
    val rows: ImmutableList<UiSkillMatrixRow>,
) {
    companion object {
        const val LEVEL_COLUMNS = 30
    }
}

/**
 * A single matrix row. [isStat] flags the "+" attribute-bonus row (no icon).
 * [marks] has [UiSkillMatrix.LEVEL_COLUMNS] entries; `true` at index `i`
 * means this ability/stat was leveled at level `i + 1`.
 */
data class UiSkillMatrixRow(
    val iconUrl: ImageUrl?,
    val isStat: Boolean,
    val marks: ImmutableList<Boolean>,
)
