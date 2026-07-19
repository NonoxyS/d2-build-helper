package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.chip.D2FilterChip
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.core.match.presentation.UiMatchPlayerPosition

private val SIDE_OPTIONS = listOf(true, false)

@Composable
internal fun GuideFilterBar(
    selectedPosition: UiMatchPlayerPosition?,
    selectedSide: Boolean?,
    onPositionToggle: (UiMatchPlayerPosition) -> Unit,
    onSideToggle: (Boolean) -> Unit,
    sideFilterAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = UiMatchPlayerPosition.entries) { position ->
            D2FilterChip(
                selected = selectedPosition == position,
                onClick = { onPositionToggle(position) },
                leadingIcon = {
                    Image(
                        painter = painterResource(position.iconResource),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
                label = {
                    Text(
                        text = stringResource(MR.strings.position_short_format, position.shortNumber),
                        style = D2BuildHelperTheme.typography.captionMD,
                    )
                },
            )
        }

        if (sideFilterAvailable) {
            items(items = SIDE_OPTIONS) { isRadiant ->
                val emblem = if (isRadiant) MR.images.radiant_square else MR.images.dire_square
                val labelRes = if (isRadiant) MR.strings.side_radiant else MR.strings.side_dire
                D2FilterChip(
                    selected = selectedSide == isRadiant,
                    onClick = { onSideToggle(isRadiant) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(emblem),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(labelRes),
                            style = D2BuildHelperTheme.typography.captionMD,
                        )
                    },
                )
            }
        }
    }
}
