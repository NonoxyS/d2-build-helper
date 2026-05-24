package dev.nonoxy.d2buildhelper.common.ui.compose.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Suppress("CompositionLocalAllowlist")
internal val LocalD2BuildHelperShapes = staticCompositionLocalOf<D2BuildHelperShapes> {
    error("CompositionLocal LocalD2BuildHelperShapes was not provided")
}

@Immutable
class D2BuildHelperShapes internal constructor(
    val cornerRadius4: CornerBasedShape = RoundedCornerShape(4.dp),
    val cornerRadius8: CornerBasedShape = RoundedCornerShape(8.dp),
    val cornerRadius12: CornerBasedShape = RoundedCornerShape(12.dp),
    val cornerRadius16: CornerBasedShape = RoundedCornerShape(16.dp),
)
