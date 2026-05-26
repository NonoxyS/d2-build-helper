package dev.nonoxy.d2buildhelper

import androidx.compose.ui.window.ComposeUIViewController
import dev.nonoxy.d2buildhelper.core.di.initKoin
import dev.nonoxy.d2buildhelper.core.network.di.NetworkEnvironmentDi
import dev.nonoxy.d2buildhelper.core.network.ktor.NetworkEnvironment
import dev.nonoxy.d2buildhelper.image.setupImageLoader
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import platform.UIKit.UIViewController

fun MainViewController(environment: NetworkEnvironment): UIViewController = ComposeUIViewController(
    configure = {
        Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
        initKoin()
        NetworkEnvironmentDi().insertKoin(environment)
        setupImageLoader()
    },
) {
    App()
}
