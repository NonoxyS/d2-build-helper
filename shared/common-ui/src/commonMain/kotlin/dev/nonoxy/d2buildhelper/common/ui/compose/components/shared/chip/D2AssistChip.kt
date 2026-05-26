@file:Suppress("ComposableParametersOrdering", "TopLevelComposableFunctions")

package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

private const val DISABLED_ALPHA = 0.38f

@Composable
fun D2AssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = D2BuildHelperTheme.shapes.cornerRadius8,
    colors: ChipColors = D2AssistChipDefaults.colors(),
    elevation: ChipElevation? = AssistChipDefaults.assistChipElevation(),
    border: BorderStroke? = D2AssistChipDefaults.border(),
    interactionSource: MutableInteractionSource? = null,
) {
    AssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        interactionSource = interactionSource,
    )
}

object D2AssistChipDefaults {
    @Composable
    fun colors(): ChipColors {
        val surface = D2BuildHelperTheme.colors.surface
        val textPrimary = D2BuildHelperTheme.colors.textPrimary
        val textSecondary = D2BuildHelperTheme.colors.textSecondary
        return AssistChipDefaults.assistChipColors(
            containerColor = surface,
            labelColor = textPrimary,
            leadingIconContentColor = textSecondary,
            trailingIconContentColor = textSecondary,
            disabledContainerColor = surface,
            disabledLabelColor = textPrimary.copy(alpha = DISABLED_ALPHA),
            disabledLeadingIconContentColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            disabledTrailingIconContentColor = textSecondary.copy(alpha = DISABLED_ALPHA),
        )
    }

    @Composable
    fun border(): BorderStroke = BorderStroke(width = 1.dp, color = D2BuildHelperTheme.colors.outline)
}
