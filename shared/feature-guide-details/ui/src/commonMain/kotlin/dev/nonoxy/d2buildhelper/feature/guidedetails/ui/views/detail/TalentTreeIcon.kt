package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

// TODO talent-tree asset: minimal Compose rendering of the 4-tier talent tree
//  (central trunk + two petals per tier). [tiersTaken] order = 10/15/20/25.
private val TALENT_TREE_WIDTH = 26.dp
private val TALENT_TREE_HEIGHT = 30.dp

/**
 * Talent-tree glyph mirroring the prototype SVG: a vertical trunk with four
 * tiers of two petals each. The left petal of a tier is lit (gold) when that
 * tier has a talent taken; otherwise both petals are muted.
 */
@Composable
internal fun TalentTreeIcon(
    tiersTaken: ImmutableList<Boolean>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(width = TALENT_TREE_WIDTH, height = TALENT_TREE_HEIGHT)) {
        val trunkX = size.width / 2f
        drawLine(
            color = TALENT_EMPTY,
            start = Offset(trunkX, size.height * 0.1f),
            end = Offset(trunkX, size.height * 0.9f),
            strokeWidth = size.width * 0.08f,
        )

        val petalRadius = size.width * 0.12f
        val leftX = size.width * 0.23f
        val rightX = size.width * 0.77f
        val tiers = listOf(0.2f, 0.43f, 0.66f, 0.9f)
        tiers.forEachIndexed { index, yFraction ->
            val taken = tiersTaken.getOrNull(index) == true
            val y = size.height * yFraction
            drawCircle(
                color = if (taken) TALENT_GOLD else TALENT_EMPTY,
                radius = petalRadius,
                center = Offset(leftX, y),
            )
            drawCircle(
                color = TALENT_EMPTY,
                radius = petalRadius,
                center = Offset(rightX, y),
            )
        }
    }
}
