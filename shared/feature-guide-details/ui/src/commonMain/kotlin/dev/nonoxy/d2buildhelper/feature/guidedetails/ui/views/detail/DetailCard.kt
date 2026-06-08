package dev.nonoxy.d2buildhelper.feature.guidedetails.ui.views.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space12
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@Composable
internal fun DetailCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                color = D2BuildHelperTheme.colors.surface,
                shape = D2BuildHelperTheme.shapes.cornerRadius12,
            )
            .border(
                width = 1.dp,
                color = D2BuildHelperTheme.colors.outline,
                shape = D2BuildHelperTheme.shapes.cornerRadius12,
            )
            .padding(14.dp),
        content = content,
    )
}

@Composable
internal fun ColumnScope.CardLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        color = D2BuildHelperTheme.colors.textSecondary,
        style = D2BuildHelperTheme.typography.captionMD,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
    Space12()
}
