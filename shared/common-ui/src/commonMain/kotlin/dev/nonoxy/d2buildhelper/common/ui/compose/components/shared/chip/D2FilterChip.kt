@file:Suppress("ComposableParametersOrdering", "TopLevelComposableFunctions")

package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

private const val SELECTED_CONTAINER_ALPHA = 0.18f
private const val DISABLED_ALPHA = 0.38f

@Composable
fun D2FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = D2BuildHelperTheme.shapes.cornerRadius8,
    colors: SelectableChipColors = D2FilterChipDefaults.colors(),
    border: BorderStroke = D2FilterChipDefaults.border(selected),
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        shape = shape,
        colors = colors,
        border = border,
    )
}

object D2FilterChipDefaults {
    @Composable
    fun colors(): SelectableChipColors {
        val colors = D2BuildHelperTheme.colors
        return FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = colors.textSecondary,
            iconColor = colors.textSecondary,
            selectedContainerColor = colors.tintColor.copy(alpha = SELECTED_CONTAINER_ALPHA),
            selectedLabelColor = colors.textPrimary,
            selectedLeadingIconColor = colors.textPrimary,
            disabledContainerColor = Color.Transparent,
            disabledLabelColor = colors.textSecondary.copy(alpha = DISABLED_ALPHA),
            disabledLeadingIconColor = colors.textSecondary.copy(alpha = DISABLED_ALPHA),
            disabledSelectedContainerColor = colors.tintColor.copy(alpha = SELECTED_CONTAINER_ALPHA * DISABLED_ALPHA),
        )
    }

    @Composable
    fun border(selected: Boolean): BorderStroke = BorderStroke(
        width = 1.dp,
        color = if (selected) D2BuildHelperTheme.colors.tintColor else D2BuildHelperTheme.colors.outline,
    )
}
