package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@Composable
fun D2Dialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = D2BuildHelperTheme.shapes.cornerRadius16,
    backgroundColor: Color = D2BuildHelperTheme.colors.surface,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    properties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(backgroundColor)
                .padding(contentPadding),
            content = content,
        )
    }
}
