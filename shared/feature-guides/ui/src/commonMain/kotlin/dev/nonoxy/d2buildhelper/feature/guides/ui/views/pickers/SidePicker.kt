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
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker

@Composable
@Suppress("UnusedParameter")
internal fun SidePicker(
    picker: UiFilterPicker.Side,
    onSideClick: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(MR.images.radiant_square),
            contentDescription = "Radiant",
            modifier = Modifier.size(64.dp).clickable { onSideClick(true) },
        )
        Image(
            painter = painterResource(MR.images.dire_square),
            contentDescription = "Dire",
            modifier = Modifier.size(64.dp).clickable { onSideClick(false) },
        )
    }
}
