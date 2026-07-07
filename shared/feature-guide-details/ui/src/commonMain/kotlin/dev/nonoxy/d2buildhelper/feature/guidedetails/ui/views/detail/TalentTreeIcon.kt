package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.TalentSide
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiTalentTier
import kotlinx.collections.immutable.ImmutableList

private val TALENT_TREE_WIDTH = 26.dp
private val TALENT_TREE_HEIGHT = 30.dp
private const val TRUNK_STROKE_FRACTION = 0.09f
private const val BRANCH_STROKE_FRACTION = 0.09f
private const val TAKEN_UNKNOWN_ALPHA = 0.5f

private val TIER_Y_FRACTIONS = listOf(0.82f, 0.60f, 0.38f, 0.16f)
private val TALENT_TIERS_ORDER = listOf(10, 15, 20, 25)

@Composable
internal fun TalentTreeIcon(
    tiers: ImmutableList<UiTalentTier>,
    modifier: Modifier = Modifier,
    width: Dp = TALENT_TREE_WIDTH,
    height: Dp = TALENT_TREE_HEIGHT,
) {
    Canvas(modifier = modifier.size(width, height)) {
        val trunkX = size.width / 2f
        drawLine(
            color = TALENT_EMPTY,
            start = Offset(trunkX, size.height * 0.06f),
            end = Offset(trunkX, size.height * 0.94f),
            strokeWidth = size.width * TRUNK_STROKE_FRACTION,
            cap = StrokeCap.Round,
        )

        val branchDx = size.width * 0.34f
        val branchDy = size.height * 0.10f
        TIER_Y_FRACTIONS.forEachIndexed { index, yFraction ->
            val y = size.height * yFraction
            val model = tiers.firstOrNull { it.tier == TALENT_TIERS_ORDER[index] }
            drawLine(
                color = branchColor(model, TalentSide.LEFT),
                start = Offset(trunkX, y),
                end = Offset(trunkX - branchDx, y - branchDy),
                strokeWidth = size.width * BRANCH_STROKE_FRACTION,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = branchColor(model, TalentSide.RIGHT),
                start = Offset(trunkX, y),
                end = Offset(trunkX + branchDx, y - branchDy),
                strokeWidth = size.width * BRANCH_STROKE_FRACTION,
                cap = StrokeCap.Round,
            )
        }
    }
}

private fun branchColor(model: UiTalentTier?, side: TalentSide) = when {
    model == null || !model.taken -> TALENT_EMPTY
    model.side == side -> TALENT_GOLD
    model.side == null -> TALENT_GOLD.copy(alpha = TAKEN_UNKNOWN_ALPHA) // talent absent from hero constants → dim both
    else -> TALENT_EMPTY
}
