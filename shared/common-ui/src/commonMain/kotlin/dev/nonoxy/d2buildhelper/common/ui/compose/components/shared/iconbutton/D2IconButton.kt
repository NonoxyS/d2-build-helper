@file:Suppress("ComposableParametersOrdering", "TopLevelComposableFunctions")

package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.iconbutton

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

private const val DISABLED_ALPHA = 0.38f

@Composable
fun D2IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = D2IconButtonDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = IconButtonDefaults.standardShape,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        shape = shape,
        content = content,
    )
}

object D2IconButtonDefaults {
    @Composable
    fun colors(): IconButtonColors {
        val textPrimary = D2BuildHelperTheme.colors.textPrimary
        val textSecondary = D2BuildHelperTheme.colors.textSecondary
        return IconButtonDefaults.iconButtonColors(
            containerColor = Color.Unspecified,
            contentColor = textPrimary,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = textSecondary.copy(alpha = DISABLED_ALPHA),
        )
    }
}
