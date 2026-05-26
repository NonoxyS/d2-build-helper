package dev.nonoxy.d2buildhelper.feature.guides.ui.views.pickers

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiMatchPlayerPosition

@Composable
internal fun PositionPicker(
    picker: UiFilterPicker.Position,
    onPositionClick: (UiMatchPlayerPosition) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        picker.options.fastForEach { option ->
            val positionLabel = stringResource(MR.strings.position_short_format, option.shortNumber)
            Image(
                painter = painterResource(option.iconResource),
                contentDescription = positionLabel,
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        onClickLabel = positionLabel,
                        role = Role.Button,
                    ) { onPositionClick(option) },
            )
        }
    }
}
