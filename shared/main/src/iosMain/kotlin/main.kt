import androidx.compose.ui.window.ComposeUIViewController
import dev.nonoxy.d2buildhelper.App
import dev.nonoxy.d2buildhelper.core.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = {
        Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
        initKoin()
    }
) {
    App()
}
