package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

private val POPUP_MAX_WIDTH = 240.dp
private val POPUP_PADDING = 12.dp
private val POPUP_GAP = 6.dp

// Wraps a tappable element and shows an anchored explain tooltip right below it, dismissed by a tap outside.
@Composable
internal fun ExplainAnchor(
    title: String,
    body: String?,
    modifier: Modifier = Modifier,
    meta: String? = null,
    content: @Composable (onClick: () -> Unit) -> Unit,
) {
    var shown by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        content { shown = true }

        if (shown) {
            val gap = with(LocalDensity.current) { POPUP_GAP.roundToPx() }
            Popup(
                alignment = Alignment.BottomCenter,
                offset = IntOffset(x = 0, y = gap),
                onDismissRequest = { shown = false },
                properties = PopupProperties(focusable = true),
            ) {
                ExplainCard(title = title, body = body, meta = meta)
            }
        }
    }
}

@Composable
private fun ExplainCard(
    title: String,
    body: String?,
    meta: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(max = POPUP_MAX_WIDTH)
            .clip(D2BuildHelperTheme.shapes.cornerRadius8)
            .background(D2BuildHelperTheme.colors.surface)
            .border(
                width = 1.dp,
                color = D2BuildHelperTheme.colors.outline,
                shape = D2BuildHelperTheme.shapes.cornerRadius8,
            )
            .padding(POPUP_PADDING),
    ) {
        Text(
            text = title,
            color = D2BuildHelperTheme.colors.textPrimary,
            style = D2BuildHelperTheme.typography.bodyLG,
        )

        if (!body.isNullOrBlank()) {
            Space4()

            Text(
                text = body,
                color = D2BuildHelperTheme.colors.textSecondary,
                style = D2BuildHelperTheme.typography.captionMD,
            )
        }

        if (!meta.isNullOrBlank()) {
            Space8()

            Text(
                text = meta,
                color = D2BuildHelperTheme.colors.textSecondary,
                style = D2BuildHelperTheme.typography.captionMD,
            )
        }
    }
}
