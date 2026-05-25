package dev.nonoxy.d2buildhelper.common.ui.compose.components.shared.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import dev.nonoxy.d2buildhelper.core.domain.models.ImageUrl

@Composable
fun D2AsyncImage(
    model: ImageUrl?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    AsyncImage(
        model = model?.raw,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}
