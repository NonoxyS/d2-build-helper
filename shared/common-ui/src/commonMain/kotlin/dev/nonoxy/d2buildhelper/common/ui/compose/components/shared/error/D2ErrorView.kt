package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space.Space
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@Composable
fun D2ErrorView(
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    messageStyle: TextStyle = D2BuildHelperTheme.typography.bodyLG,
    messageColor: Color = D2BuildHelperTheme.colors.textPrimary,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = D2BuildHelperTheme.colors.tintColor,
        contentColor = D2BuildHelperTheme.colors.textPrimary,
    ),
    contentPadding: PaddingValues = PaddingValues(24.dp),
    spaceBetween: Dp = 16.dp,
) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, style = messageStyle, color = messageColor)
        Space(height = spaceBetween)
        Button(onClick = onRetry, colors = buttonColors) {
            Text(text = retryLabel)
        }
    }
}
