package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@Composable
fun D2LoadingView(
    modifier: Modifier = Modifier,
    color: Color = D2BuildHelperTheme.colors.tintColor,
    backgroundColor: Color = Color.Transparent,
    contentAlignment: Alignment = Alignment.Center,
) {
    Box(
        modifier = modifier.background(backgroundColor),
        contentAlignment = contentAlignment,
    ) {
        CircularProgressIndicator(color = color)
    }
}
