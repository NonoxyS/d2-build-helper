package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl

private val GLOW_BLUR_RADIUS = 6.dp
private const val GLOW_ALPHA = 0.6f
private val RING_WIDTH = 1.5.dp

/**
 * Hero icon with a colored silhouette glow (prototype `.team.enemy/.ally .hc`).
 *
 * Cross-platform glow strategy:
 * - A tinted blurred copy of the icon is drawn underneath via [Modifier.blur].
 *   This renders on iOS (Skia) and on Android API 31+ (RenderEffect). On
 *   Android API 26-30 `Modifier.blur` is a no-op (graceful, never crashes).
 * - A colored ring is ALWAYS drawn around the icon as the reliable fallback so
 *   the team tint reads on every platform/SDK regardless of blur support.
 */
@Composable
internal fun HeroGlowIcon(
    iconUrl: ImageUrl?,
    glowColor: Color,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 36.dp,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Blurred tinted copy underneath (silhouette glow where blur is supported).
        AsyncImage(
            model = iconUrl?.raw,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(iconSize)
                .blur(GLOW_BLUR_RADIUS)
                .clip(D2BuildHelperTheme.shapes.cornerRadius8),
            colorFilter = ColorFilter.tint(glowColor.copy(alpha = GLOW_ALPHA)),
        )

        // Crisp icon + colored ring fallback (always visible).
        AsyncImage(
            model = iconUrl?.raw,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(iconSize)
                .clip(D2BuildHelperTheme.shapes.cornerRadius8)
                .border(
                    width = RING_WIDTH,
                    color = glowColor,
                    shape = D2BuildHelperTheme.shapes.cornerRadius8,
                ),
        )
    }
}
