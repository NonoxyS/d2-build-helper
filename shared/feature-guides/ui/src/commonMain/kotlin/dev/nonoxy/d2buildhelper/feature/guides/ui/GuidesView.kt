package dev.nonoxy.d2buildhelper.feature.guides.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar.D2TopBar
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterChip
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState
import dev.nonoxy.d2buildhelper.feature.guides.ui.views.GuideListView

@Composable
internal fun GuidesView(
    state: UiGuidesState,
    onFilterChipClick: (GuidesFilterKind) -> Unit,
    onFilterReset: (GuidesFilterKind) -> Unit,
) {
    Column(modifier = Modifier.systemBarsPadding().fillMaxSize()) {
        D2TopBar(title = stringResource(MR.strings.all_heroes))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(state.filterChips, key = { it.kind::class.simpleName!! }) { chip ->
                FilterChipRow(chip = chip, onClick = onFilterChipClick, onReset = onFilterReset)
            }
        }

        GuideListView(guides = state.guides)
    }
}

@Composable
private fun FilterChipRow(
    chip: UiFilterChip,
    onClick: (GuidesFilterKind) -> Unit,
    onReset: (GuidesFilterKind) -> Unit,
) {
    AssistChip(
        onClick = { onClick(chip.kind) },
        label = { Text(text = chip.label, style = D2BuildHelperTheme.typography.captionMD) },
        trailingIcon = if (chip.isApplied) {
            {
                IconButton(onClick = { onReset(chip.kind) }, modifier = Modifier.size(20.dp)) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                }
            }
        } else {
            null
        },
        colors = AssistChipDefaults.assistChipColors(
            labelColor = D2BuildHelperTheme.colors.textPrimary,
        ),
    )
}
