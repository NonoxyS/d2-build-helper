package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineup
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiLineupMember
import kotlinx.collections.immutable.ImmutableList

private val HERO_PLAQUE_SIZE = 50.dp
private val HERO_ICON_SIZE = 36.dp

@Composable
internal fun LineupCard(
    lineup: UiLineup,
    modifier: Modifier = Modifier,
) {
    var explained by remember { mutableStateOf<UiLineupMember?>(null) }

    DetailCard(modifier = modifier) {
        CardLabel(text = stringResource(MR.strings.guide_detail_card_lineup))

        TeamRow(
            members = lineup.enemies,
            glowColor = ENEMY_GLOW_COLOR,
            onMemberClick = { explained = it },
        )
        Space8()

        TeamRow(
            members = lineup.allies,
            glowColor = ALLY_GLOW_COLOR,
            onMemberClick = { explained = it },
        )
    }

    explained?.let { member ->
        ExplainPopup(
            title = stringResource(MR.strings.guide_detail_card_lineup),
            body = member.role,
            onDismissRequest = { explained = null },
        )
    }
}

@Composable
private fun TeamRow(
    members: ImmutableList<UiLineupMember>,
    glowColor: Color,
    onMemberClick: (UiLineupMember) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        members.fastForEach { member ->
            HeroPlaque(
                member = member,
                glowColor = glowColor,
                onClick = { onMemberClick(member) },
            )
        }
    }
}

@Composable
private fun HeroPlaque(
    member: UiLineupMember,
    glowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(HERO_PLAQUE_SIZE)
            .then(
                if (member.isMe) {
                    Modifier.background(
                        color = D2BuildHelperTheme.colors.surfaceVariant,
                        shape = D2BuildHelperTheme.shapes.cornerRadius8,
                    )
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        HeroGlowIcon(
            iconUrl = member.heroIconUrl,
            glowColor = glowColor,
            contentDescription = member.role,
            iconSize = HERO_ICON_SIZE,
        )
    }
}
