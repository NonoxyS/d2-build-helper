package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.chip.D2InputChip
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.icon.D2Icon
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.iconbutton.D2IconButton
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiHeroFilter

@Composable
internal fun HeroFilterChip(
    heroFilter: UiHeroFilter?,
    onClick: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    D2InputChip(
        selected = heroFilter != null,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = heroFilter?.displayName ?: stringResource(MR.strings.all_heroes),
                style = D2BuildHelperTheme.typography.captionMD,
            )
        },
        leadingIcon = heroFilter?.let { applied ->
            {
                AsyncImage(
                    model = applied.iconUrl.raw,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        trailingIcon = heroFilter?.let {
            {
                D2IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(20.dp),
                ) {
                    D2Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(MR.strings.content_desc_clear),
                    )
                }
            }
        },
    )
}
