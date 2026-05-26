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
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiFilterPicker

@Composable
internal fun SidePicker(
    picker: UiFilterPicker.Side,
    onSideClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val radiantLabel = stringResource(MR.strings.content_desc_radiant)
        Image(
            painter = painterResource(MR.images.radiant_square),
            contentDescription = radiantLabel,
            modifier = Modifier
                .size(64.dp)
                .clickable(
                    onClickLabel = radiantLabel,
                    role = Role.Button,
                ) { onSideClick(true) },
        )
        val direLabel = stringResource(MR.strings.content_desc_dire)
        Image(
            painter = painterResource(MR.images.dire_square),
            contentDescription = direLabel,
            modifier = Modifier
                .size(64.dp)
                .clickable(
                    onClickLabel = direLabel,
                    role = Role.Button,
                ) { onSideClick(false) },
        )
    }
}
