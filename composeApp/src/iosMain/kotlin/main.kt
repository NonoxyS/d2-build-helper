import androidx.compose.ui.window.ComposeUIViewController
import dev.nonoxy.d2buildhelper.App
import dev.nonoxy.d2buildhelper.core.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = { initKoin() }
) {
    App()
}
