package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@Composable
fun D2TopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleStyle: TextStyle = D2BuildHelperTheme.typography.bodyLG,
    titleColor: Color = D2BuildHelperTheme.colors.textPrimary,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    windowInsets: WindowInsets = WindowInsets.statusBars,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier.windowInsetsPadding(windowInsets)
            .then(
                modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
    ) {
        leading?.invoke()
        if (title != null) {
            Text(text = title, style = titleStyle, color = titleColor)
        }
        trailing?.invoke()
    }
}
