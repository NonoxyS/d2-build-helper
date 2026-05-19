package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.FileContentReader
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

internal actual fun Module.bindFileContentReader() {
    single { FileContentReader(androidContext()) }
}
