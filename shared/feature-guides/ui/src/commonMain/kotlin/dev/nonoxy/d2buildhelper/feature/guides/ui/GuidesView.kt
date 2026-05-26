package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.chip.D2AssistChip
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.iconbutton.D2IconButton
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar.D2TopBar
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterChip
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideListView

@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onFilterChipClick: (GuidesFilterKind) -> Unit,
    onFilterReset: (GuidesFilterKind) -> Unit,
) {
    Scaffold(
        containerColor = D2BuildHelperTheme.colors.background,
        topBar = { D2TopBar(title = stringResource(MR.strings.all_heroes)) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(state.filterChips, key = { it.kind }) { chip ->
                    FilterChipRow(chip = chip, onClick = onFilterChipClick, onReset = onFilterReset)
                }
            }

            GuideListView(guides = state.guides)
        }
    }
}

@Composable
private fun FilterChipRow(
    chip: UiFilterChip,
    onClick: (GuidesFilterKind) -> Unit,
    onReset: (GuidesFilterKind) -> Unit,
) {
    D2AssistChip(
        onClick = { onClick(chip.kind) },
        label = { Text(text = chipLabel(chip), style = D2BuildHelperTheme.typography.captionMD) },
        trailingIcon = if (chip.isApplied) {
            {
                D2IconButton(onClick = { onReset(chip.kind) }, modifier = Modifier.size(20.dp)) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                }
            }
        } else {
            null
        },
    )
}

@Composable
private fun chipLabel(chip: UiFilterChip): String {
    val title = stringResource(
        when (chip.kind) {
            GuidesFilterKind.Hero -> MR.strings.filter_chip_hero
            GuidesFilterKind.Position -> MR.strings.filter_chip_position
            GuidesFilterKind.Side -> MR.strings.filter_chip_side
        },
    )
    val appliedValue: String? = when (chip) {
        is UiFilterChip.Hero -> chip.appliedHeroName
        is UiFilterChip.Position -> chip.appliedPosition?.let {
            stringResource(MR.strings.position_short_format, it.shortNumber)
        }

        is UiFilterChip.Side -> chip.appliedIsRadiant?.let {
            stringResource(if (it) MR.strings.side_radiant else MR.strings.side_dire)
        }
    }
    return appliedValue
        ?.let { stringResource(MR.strings.filter_chip_applied_format, title, it) }
        ?: title
}
