import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.icerock.moko.resources.compose.stringResource
import dev.nonoxy.d2buildhelper.App
import dev.nonoxy.d2buildhelper.common.resources.MR
import dev.nonoxy.d2buildhelper.core.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import java.awt.Dimension

fun main() {
    Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
    initKoin()
    application {
    Window(
        title = stringResource(MR.strings.app_name),
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
    }
}