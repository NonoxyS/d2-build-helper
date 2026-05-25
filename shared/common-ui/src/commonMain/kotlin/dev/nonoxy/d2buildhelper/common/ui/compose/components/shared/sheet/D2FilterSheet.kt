package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun D2FilterSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier.fillMaxWidth().heightIn(min = 300.dp),
        containerColor = D2BuildHelperTheme.colors.surface,
        contentColor = D2BuildHelperTheme.colors.textPrimary,
        content = content,
    )
}
