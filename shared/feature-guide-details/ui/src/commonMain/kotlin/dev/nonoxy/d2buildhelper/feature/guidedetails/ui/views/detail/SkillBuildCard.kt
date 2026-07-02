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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiSkillMatrixRow
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiTalent
import androidx.compose.ui.util.fastForEach
import kotlinx.collections.immutable.ImmutableList

private val ABILITY_ICON_SIZE = 34.dp
private val SCEPTER_ICON_SIZE = 34.dp
private val POINT_DOT_SIZE = 5.dp
private val ROW_LABEL_WIDTH = 28.dp
private val CELL_SIZE = 28.dp
private val CELL_GAP = 2.dp
private val CELL_CORNER = RoundedCornerShape(4.dp)
private val ROW_V_PADDING = 3.dp
private const val SCEPTER_GREY_ALPHA = 0.3f

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
    var explainedTalent by remember { mutableStateOf<UiTalent?>(null) }
    var explainedAbilityIndex by remember { mutableStateOf<Int?>(null) }

    DetailCard(modifier = modifier) {
        CardLabel(text = stringResource(MR.strings.guide_detail_card_skill_build))

        SummaryRow(
            skillBuild = skillBuild,
            onAbilityClick = { index -> explainedAbilityIndex = index },
        )
        Space6()

        SkillMatrix(matrix = skillBuild.matrix)
        Space4()

        Text(
            text = stringResource(MR.strings.guide_detail_skill_matrix_caption),
            color = D2BuildHelperTheme.colors.textSecondary,
            style = D2BuildHelperTheme.typography.captionMD,
        )

        if (skillBuild.talents.isNotEmpty()) {
            Space6()

            TalentList(
                tiersTaken = skillBuild.summary.talentTierTaken,
                talents = skillBuild.talents,
                onTalentClick = { talent -> explainedTalent = talent },
            )
        }
    }

    explainedTalent?.let { talent ->
        ExplainPopup(
            title = "${talent.level}",
            body = talent.text,
            onDismissRequest = { explainedTalent = null },
        )
    }

    explainedAbilityIndex?.let { index ->
        val ability = skillBuild.summary.abilities.getOrNull(index)
        val fallbackTitle = stringResource(MR.strings.guide_detail_card_skill_build)
        ExplainPopup(
            title = ability?.name?.takeIf { it.isNotBlank() } ?: fallbackTitle,
            body = ability?.let { "${it.pointCount}" },
            onDismissRequest = { explainedAbilityIndex = null },
        )
    }
}

@Composable
private fun SummaryRow(
    skillBuild: UiSkillBuild,
    onAbilityClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TalentTreeIcon(tiersTaken = skillBuild.summary.talentTierTaken)

        skillBuild.summary.abilities.forEachIndexed { index, ability ->
            AbilitySummaryColumn(
                ability = ability,
                tint = abilityTints.getOrElse(index) { abilityTints.last() },
                onClick = { onAbilityClick(index) },
            )
        }

        ScepterChip(purchased = skillBuild.summary.scepterPurchased)
    }
}

@Composable
private fun AbilitySummaryColumn(
    ability: UiAbilitySummary,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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

        PointDots(pointCount = ability.earlyPointCount, maxPoints = if (ability.isUltimate) 3 else 4)
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

        matrix.rows.fastForEach { row ->
            MatrixGridline()
            MatrixRow(row = row)
        }
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
private fun MatrixRow(
    row: UiSkillMatrixRow,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = ROW_V_PADDING),
        horizontalArrangement = Arrangement.spacedBy(CELL_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RowLabel(row = row)
        row.marks.fastForEach { marked ->
            MatrixCell(row = row, marked = marked)
        }
    }
}

@Composable
private fun MatrixCell(
    row: UiSkillMatrixRow,
    marked: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(CELL_SIZE), contentAlignment = Alignment.Center) {
        if (!marked) return@Box
        if (row.isStat) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CELL_CORNER)
                    .background(ABILITY_STAT_COLOR),
            )
        } else {
            AsyncImage(
                model = row.iconUrl?.raw,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CELL_CORNER),
            )
        }
    }
}

@Composable
private fun RowLabel(
    row: UiSkillMatrixRow,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(width = ROW_LABEL_WIDTH, height = CELL_SIZE),
        contentAlignment = Alignment.Center,
    ) {
        if (row.isStat) {
            Text(
                text = stringResource(MR.strings.guide_detail_skill_stat_row),
                color = D2BuildHelperTheme.colors.textSecondary,
                style = D2BuildHelperTheme.typography.captionMD,
            )
        } else {
            AsyncImage(
                model = row.iconUrl?.raw,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(CELL_SIZE).clip(CELL_CORNER),
            )
        }
    }
}

@Composable
private fun TalentList(
    tiersTaken: ImmutableList<Boolean>,
    talents: ImmutableList<UiTalent>,
    onTalentClick: (UiTalent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        TalentTreeIcon(tiersTaken = tiersTaken)
        Space6()

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            talents.fastForEach { talent ->
                Row(
                    modifier = Modifier.clickable { onTalentClick(talent) },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "${talent.level}",
                        color = TALENT_GOLD,
                        style = D2BuildHelperTheme.typography.captionMD,
                        modifier = Modifier.padding(end = 2.dp),
                    )
                    Text(
                        text = talent.text,
                        color = D2BuildHelperTheme.colors.textPrimary,
                        style = D2BuildHelperTheme.typography.captionMD,
                    )
                }
            }
        }
    }
}
