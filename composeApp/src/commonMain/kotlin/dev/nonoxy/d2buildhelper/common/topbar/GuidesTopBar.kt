package dev.nonoxy.d2buildhelper.common.topbar

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.nonoxy.d2buildhelper.theme.D2BuildHelperTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GuidesTopBar() {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(red = 255, green = 255, blue = 255),
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
                    top = 9.dp,
                    bottom = 9.dp
                )
        )
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 1.dp,
            color = Color(red = 255, green = 255, blue = 255)
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