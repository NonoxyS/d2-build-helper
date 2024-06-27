package dev.nonoxy.d2buildhelper.common.topbar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            .height(IntrinsicSize.Min)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = .14f),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Text(
            text = "Все герои",
            style = MaterialTheme.typography.bodySmall,
            color = Color(red = 255, green = 255, blue = 255, alpha = 0xCC),
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
            color = Color.White.copy(alpha = .14f)
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                tint = Color(red = 255, green = 255, blue = 255, alpha = 0xCC),
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