@file:Suppress("ComposableParametersOrdering", "TopLevelComposableFunctions")

package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

private const val SELECTED_CONTAINER_ALPHA = 0.18f
private const val DISABLED_ALPHA = 0.38f

@Composable
fun D2InputChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = D2BuildHelperTheme.shapes.cornerRadius8,
    colors: SelectableChipColors = D2InputChipDefaults.colors(),
    border: BorderStroke = D2InputChipDefaults.border(selected),
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        border = border,
    )
}

object D2InputChipDefaults {
    @Composable
    fun colors(): SelectableChipColors {
        val colors = D2BuildHelperTheme.colors
        return InputChipDefaults.inputChipColors(
            containerColor = colors.surface,
            labelColor = colors.textPrimary,
            leadingIconColor = colors.textSecondary,
            trailingIconColor = colors.textSecondary,
            disabledContainerColor = colors.surface,
            disabledLabelColor = colors.textPrimary.copy(alpha = DISABLED_ALPHA),
            disabledLeadingIconColor = colors.textSecondary.copy(alpha = DISABLED_ALPHA),
            disabledTrailingIconColor = colors.textSecondary.copy(alpha = DISABLED_ALPHA),
            selectedContainerColor = colors.tintColor.copy(alpha = SELECTED_CONTAINER_ALPHA),
            disabledSelectedContainerColor = colors.tintColor.copy(alpha = SELECTED_CONTAINER_ALPHA * DISABLED_ALPHA),
            selectedLabelColor = colors.textPrimary,
            selectedLeadingIconColor = colors.textPrimary,
            selectedTrailingIconColor = colors.textPrimary,
        )
    }

    @Composable
    fun border(selected: Boolean): BorderStroke = BorderStroke(
        width = 1.dp,
        color = if (selected) D2BuildHelperTheme.colors.tintColor else D2BuildHelperTheme.colors.outline,
    )
}
