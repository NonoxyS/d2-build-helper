package dev.nonoxy.d2buildhelper.feature.guides.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.nonoxy.d2buildhelper.common.resources.Res
import dev.nonoxy.d2buildhelper.common.resources.error_loading
import dev.nonoxy.d2buildhelper.common.resources.retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun GuidesErrorView(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(Res.string.error_loading))
        Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
    }
}
