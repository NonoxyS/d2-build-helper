package dev.nonoxy.d2buildhelper.app

import android.app.Application
import dev.nonoxy.d2buildhelper.BuildConfig
import dev.nonoxy.d2buildhelper.core.di.initKoin
import dev.nonoxy.d2buildhelper.di.appModule
import dev.nonoxy.d2buildhelper.image.setupImageLoader
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext

class D2BHApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupLogging()
        setupKoin()
        setupImageLoader()
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
        }
    }

    private fun setupKoin() {
        initKoin {
            androidContext(this@D2BHApplication)
            modules(appModule)
        }
    }
}
