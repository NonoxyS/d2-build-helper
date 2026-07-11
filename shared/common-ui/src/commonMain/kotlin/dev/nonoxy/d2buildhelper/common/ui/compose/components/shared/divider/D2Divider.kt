package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.divider

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@Composable
fun D2Divider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = D2BuildHelperTheme.colors.outline,
    )
}
