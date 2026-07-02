package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.icon.D2Icon
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space6
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiAbilitySummary
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillBuild
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrix
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrixBottomCell
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrixRow
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiTalentTier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val ABILITY_ICON_SIZE = 34.dp
private val SCEPTER_ICON_SIZE = 34.dp
private val POINT_DOT_SIZE = 5.dp
private val ROW_LABEL_WIDTH = 28.dp
private val CELL_SIZE = 28.dp
private val CELL_GAP = 2.dp
private val CELL_CORNER = RoundedCornerShape(4.dp)
private val ROW_V_PADDING = 3.dp
private val STAT_MARK_SIZE = 16.dp
private const val SCEPTER_GREY_ALPHA = 0.3f
private const val MAX_EARLY_DOTS = 3

private val abilityTints = listOf(
    ABILITY_Q_COLOR,
    ABILITY_W_COLOR,
    ABILITY_E_COLOR,
    ABILITY_R_COLOR,
)

@Composable
internal fun SkillBuildCard(
    skillBuild: UiSkillBuild,
    modifier: Modifier = Modifier,
) {
    DetailCard(modifier = modifier) {
        CardLabel(text = stringResource(MR.strings.guide_detail_card_skill_build))

        SummaryRow(skillBuild = skillBuild)
        Space6()

        SkillMatrix(matrix = skillBuild.matrix)
    }
}

@Composable
private fun SummaryRow(
    skillBuild: UiSkillBuild,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TalentTreeIcon(tiers = skillBuild.summary.talentTiers)

        skillBuild.summary.abilities.forEachIndexed { index, ability ->
            AbilitySummaryColumn(
                ability = ability,
                tint = abilityTints.getOrElse(index) { abilityTints.last() },
            )
        }

        ScepterChip(purchased = skillBuild.summary.scepterPurchased)
    }
}

@Composable
private fun AbilitySummaryColumn(
    ability: UiAbilitySummary,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val fallbackTitle = stringResource(MR.strings.guide_detail_card_skill_build)
    ExplainAnchor(
        title = ability.name?.takeIf { it.isNotBlank() } ?: fallbackTitle,
        body = stringResource(MR.strings.guide_detail_ability_points, ability.pointCount),
        modifier = modifier,
    ) { onClick ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ability.iconUrl?.raw,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ABILITY_ICON_SIZE)
                    .background(color = tint, shape = D2BuildHelperTheme.shapes.cornerRadius8)
                    .clip(D2BuildHelperTheme.shapes.cornerRadius8)
                    .clickable(onClick = onClick),
            )
            Space4()

            PointDots(pointCount = ability.earlyPointCount, maxPoints = MAX_EARLY_DOTS)
        }
    }
}

@Composable
private fun PointDots(
    pointCount: Int,
    maxPoints: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(maxPoints) { index ->
            Box(
                modifier = Modifier
                    .size(POINT_DOT_SIZE)
                    .background(
                        color = if (index < pointCount) TALENT_GOLD else TALENT_EMPTY,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun ScepterChip(
    purchased: Boolean,
    modifier: Modifier = Modifier,
) {
    D2Icon(
        painter = painterResource(MR.images.aghanim),
        contentDescription = null,
        tint = D2BuildHelperTheme.colors.textPrimary,
        modifier = modifier
            .size(SCEPTER_ICON_SIZE)
            .alpha(if (purchased) 1f else SCEPTER_GREY_ALPHA),
    )
}

@Composable
private fun SkillMatrix(
    matrix: UiSkillMatrix,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.horizontalScroll(rememberScrollState())) {
        HeaderRow()

        matrix.abilityRows.fastForEach { row ->
            MatrixGridline()
            AbilityMatrixRow(row = row)
        }
        MatrixGridline()
        BottomMatrixRow(cells = matrix.bottomCells)
        MatrixGridline()
    }
}

@Composable
private fun MatrixGridline(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, thickness = 1.dp, color = D2BuildHelperTheme.colors.outline)
}

@Composable
private fun HeaderRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = ROW_V_PADDING),
        horizontalArrangement = Arrangement.spacedBy(CELL_GAP),
    ) {
        Box(modifier = Modifier.width(ROW_LABEL_WIDTH))
        repeat(UiSkillMatrix.LEVEL_COLUMNS) { index ->
            Box(modifier = Modifier.size(CELL_SIZE), contentAlignment = Alignment.Center) {
                Text(
                    text = "${index + 1}",
                    color = D2BuildHelperTheme.colors.textSecondary,
                    style = D2BuildHelperTheme.typography.captionMD,
                )
            }
        }
    }
}

@Composable
private fun AbilityMatrixRow(
    row: UiSkillMatrixRow,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = ROW_V_PADDING),
        horizontalArrangement = Arrangement.spacedBy(CELL_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(width = ROW_LABEL_WIDTH, height = CELL_SIZE),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = row.iconUrl?.raw,
                contentDescription = row.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(CELL_SIZE).clip(CELL_CORNER),
            )
        }
        row.marks.fastForEach { marked ->
            Box(modifier = Modifier.size(CELL_SIZE), contentAlignment = Alignment.Center) {
                if (marked) {
                    AsyncImage(
                        model = row.iconUrl?.raw,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CELL_CORNER),
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomMatrixRow(
    cells: ImmutableList<UiSkillMatrixBottomCell>,
    modifier: Modifier = Modifier,
) {
    val talentTitle = stringResource(MR.strings.guide_detail_card_skill_build)
    Row(
        modifier = modifier.padding(vertical = ROW_V_PADDING),
        horizontalArrangement = Arrangement.spacedBy(CELL_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(ROW_LABEL_WIDTH))
        cells.fastForEach { cell ->
            Box(modifier = Modifier.size(CELL_SIZE), contentAlignment = Alignment.Center) {
                when (cell) {
                    UiSkillMatrixBottomCell.Empty -> Unit
                    UiSkillMatrixBottomCell.Stat -> Box(
                        modifier = Modifier
                            .size(STAT_MARK_SIZE)
                            .clip(CELL_CORNER)
                            .background(ABILITY_STAT_COLOR),
                    )
                    is UiSkillMatrixBottomCell.Talent -> ExplainAnchor(
                        title = talentTitle,
                        body = cell.text,
                    ) { onClick ->
                        TalentTreeIcon(
                            tiers = persistentListOf(
                                UiTalentTier(tier = cell.tier, taken = true, side = cell.side),
                            ),
                            width = CELL_SIZE,
                            height = CELL_SIZE,
                            modifier = Modifier.clickable(onClick = onClick),
                        )
                    }
                }
            }
        }
    }
}
