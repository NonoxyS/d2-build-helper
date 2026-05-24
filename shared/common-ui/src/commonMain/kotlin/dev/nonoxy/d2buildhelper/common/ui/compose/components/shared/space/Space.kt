@file:Suppress("TooManyFunctions")

package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.space

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Space(modifier: Modifier = Modifier, height: Dp = 0.dp, width: Dp = 0.dp) =
    Spacer(modifier = modifier.height(height).width(width))

@Composable
fun ColumnScope.Space(height: Dp, modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.height(height))

@Composable
fun RowScope.Space(width: Dp, modifier: Modifier = Modifier) =
    Spacer(modifier = modifier.width(width))

@Composable fun RowScope.Space2() = Space(width = 2.dp)

@Composable fun ColumnScope.Space2() = Space(height = 2.dp)

@Composable fun RowScope.Space4() = Space(width = 4.dp)

@Composable fun ColumnScope.Space4() = Space(height = 4.dp)

@Composable fun RowScope.Space6() = Space(width = 6.dp)

@Composable fun ColumnScope.Space6() = Space(height = 6.dp)

@Composable fun RowScope.Space8() = Space(width = 8.dp)

@Composable fun ColumnScope.Space8() = Space(height = 8.dp)

@Composable fun RowScope.Space12() = Space(width = 12.dp)

@Composable fun ColumnScope.Space12() = Space(height = 12.dp)

@Composable fun RowScope.Space16() = Space(width = 16.dp)

@Composable fun ColumnScope.Space16() = Space(height = 16.dp)

@Composable fun RowScope.Space20() = Space(width = 20.dp)

@Composable fun ColumnScope.Space20() = Space(height = 20.dp)

@Composable fun RowScope.Space24() = Space(width = 24.dp)

@Composable fun ColumnScope.Space24() = Space(height = 24.dp)

@Composable fun RowScope.Space32() = Space(width = 32.dp)

@Composable fun ColumnScope.Space32() = Space(height = 32.dp)

@Composable fun RowScope.Space40() = Space(width = 40.dp)

@Composable fun ColumnScope.Space40() = Space(height = 40.dp)

@Composable fun RowScope.Space48() = Space(width = 48.dp)

@Composable fun ColumnScope.Space48() = Space(height = 48.dp)
