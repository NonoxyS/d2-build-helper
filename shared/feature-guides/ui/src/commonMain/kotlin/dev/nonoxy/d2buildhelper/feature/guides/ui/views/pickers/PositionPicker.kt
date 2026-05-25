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
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker

@Composable
internal fun PositionPicker(
    picker: UiFilterPicker.Position,
    onPositionClick: (MatchPlayerPosition) -> Unit,
) {
    Row(
        modifier = Modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        picker.options.forEach { option ->
            val domainPosition = MatchPlayerPosition.valueOf(option.name)
            val icon = option.iconResource
            if (icon != null) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = option.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onPositionClick(domainPosition) },
                )
            }
        }
    }
}
