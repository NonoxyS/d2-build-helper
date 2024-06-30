package dev.nonoxy.d2buildhelper.common.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.theme.D2BuildHelperTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GuidesTopBar() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        HeroSelect()
    }
}

@Composable
private fun HeroSelect() {
    Row(
        modifier = Modifier
            .background(D2BuildHelperTheme.colors.primaryContainer)
            .height(IntrinsicSize.Min)
            .border(
                width = 1.dp,
                color = D2BuildHelperTheme.colors.outline,
                shape = RoundedCornerShape(4.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Все герои",
            color = D2BuildHelperTheme.colors.primaryText,
            style = D2BuildHelperTheme.typography.bodySmall,
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
        )
        VerticalDivider(
            modifier = Modifier.fillMaxHeight().padding(vertical = 1.dp),
            thickness = 1.dp,
            color = D2BuildHelperTheme.colors.outline
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                tint = D2BuildHelperTheme.colors.primaryText,
                contentDescription = null,
            )
        }
    }
}

@Composable
@Preview
private fun GuidesTopBar_Preview() {
    D2BuildHelperTheme {
        GuidesTopBar()
    }
}