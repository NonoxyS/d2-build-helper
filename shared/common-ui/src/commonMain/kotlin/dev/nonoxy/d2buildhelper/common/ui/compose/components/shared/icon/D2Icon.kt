package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@Composable
fun D2Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = D2BuildHelperTheme.colors.textPrimary,
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun D2Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = D2BuildHelperTheme.colors.textPrimary,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun D2Icon(
    bitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = D2BuildHelperTheme.colors.textPrimary,
) {
    Icon(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}
