@file:Suppress("ComposableParametersOrdering", "TopLevelComposableFunctions")

package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import dev.nonoxy.d2buildhelper.common.ui.compose.theme.D2BuildHelperTheme

private const val DISABLED_ALPHA = 0.38f

@Composable
fun D2OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = D2BuildHelperTheme.typography.textMD,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = D2BuildHelperTheme.shapes.cornerRadius8,
    colors: TextFieldColors = D2OutlinedTextFieldDefaults.colors(),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}

object D2OutlinedTextFieldDefaults {
    @Composable
    fun colors(): TextFieldColors {
        val tintColor = D2BuildHelperTheme.colors.tintColor
        val outline = D2BuildHelperTheme.colors.outline
        val textPrimary = D2BuildHelperTheme.colors.textPrimary
        val textSecondary = D2BuildHelperTheme.colors.textSecondary
        val surface = D2BuildHelperTheme.colors.surface
        return OutlinedTextFieldDefaults.colors(
            focusedTextColor = textPrimary,
            unfocusedTextColor = textPrimary,
            disabledTextColor = textPrimary.copy(alpha = DISABLED_ALPHA),
            errorTextColor = textPrimary,
            focusedContainerColor = surface,
            unfocusedContainerColor = surface,
            disabledContainerColor = surface,
            errorContainerColor = surface,
            cursorColor = tintColor,
            errorCursorColor = tintColor,
            focusedBorderColor = tintColor,
            unfocusedBorderColor = outline,
            disabledBorderColor = outline.copy(alpha = DISABLED_ALPHA),
            errorBorderColor = tintColor,
            focusedLeadingIconColor = textSecondary,
            unfocusedLeadingIconColor = textSecondary,
            disabledLeadingIconColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            errorLeadingIconColor = textSecondary,
            focusedTrailingIconColor = textSecondary,
            unfocusedTrailingIconColor = textSecondary,
            disabledTrailingIconColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            errorTrailingIconColor = textSecondary,
            focusedLabelColor = textSecondary,
            unfocusedLabelColor = textSecondary,
            disabledLabelColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            errorLabelColor = textSecondary,
            focusedPlaceholderColor = textSecondary,
            unfocusedPlaceholderColor = textSecondary,
            disabledPlaceholderColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            errorPlaceholderColor = textSecondary,
            focusedSupportingTextColor = textSecondary,
            unfocusedSupportingTextColor = textSecondary,
            disabledSupportingTextColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            errorSupportingTextColor = textSecondary,
            focusedPrefixColor = textSecondary,
            unfocusedPrefixColor = textSecondary,
            disabledPrefixColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            errorPrefixColor = textSecondary,
            focusedSuffixColor = textSecondary,
            unfocusedSuffixColor = textSecondary,
            disabledSuffixColor = textSecondary.copy(alpha = DISABLED_ALPHA),
            errorSuffixColor = textSecondary,
        )
    }
}
