package dev.nonoxy.d2buildhelper.common.ui.compose.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp

val systemBarInsets: WindowInsets
    @Composable get() = WindowInsets.systemBars
        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)

val systemBarPaddingValues: PaddingValues
    @Composable get() = systemBarInsets.asPaddingValues()

val navigationBarHeight: Dp
    @Composable get() = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

val statusBarHeight: Dp
    @Composable get() = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

val navigationBarsStart: Dp
    @Composable get() = WindowInsets.navigationBars
        .only(WindowInsetsSides.Start)
        .asPaddingValues()
        .calculateStartPadding(layoutDirection = LocalLayoutDirection.current)

val navigationBarsEnd: Dp
    @Composable get() = WindowInsets.navigationBars
        .only(WindowInsetsSides.End)
        .asPaddingValues()
        .calculateEndPadding(layoutDirection = LocalLayoutDirection.current)

val WindowInsets.Companion.Zero: WindowInsets
    get() = WindowInsets(left = 0, top = 0, right = 0, bottom = 0)

@Suppress("ModifierFactoryUnreferencedReceiver")
fun Modifier.visiblePadding(
    isVisible: Boolean,
    paddingValues: PaddingValues,
): Modifier = if (isVisible) padding(paddingValues) else padding()
