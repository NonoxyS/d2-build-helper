import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nonoxy.d2buildhelper.App
import dota_2_build_helper.composeapp.generated.resources.Res
import dota_2_build_helper.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

fun main() = application {
    Window(
        title = stringResource(Res.string.app_name),
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}