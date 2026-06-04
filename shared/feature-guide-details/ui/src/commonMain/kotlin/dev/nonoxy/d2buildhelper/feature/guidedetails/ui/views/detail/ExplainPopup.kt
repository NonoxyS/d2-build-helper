package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.dialog.D2Dialog
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space4
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space8
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

/**
 * Reusable explanation popup (skill / item / hero) — prototype `.popup`.
 * [meta] is the optional small footnote line (e.g. role/position for a hero).
 */
@Composable
internal fun ExplainPopup(
    title: String,
    body: String?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    meta: String? = null,
) {
    D2Dialog(onDismissRequest = onDismissRequest, modifier = modifier.fillMaxWidth()) {
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
