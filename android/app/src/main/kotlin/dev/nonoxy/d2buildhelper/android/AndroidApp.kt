package dev.nonoxy.d2buildhelper.android

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.nonoxy.d2buildhelper.App
import dev.nonoxy.d2buildhelper.core.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext

class AndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog(defaultTag = "D2BuildHelper"))
        initKoin { androidContext(this@AndroidApp) }
    }
}

class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}
