package dev.nonoxy.d2buildhelper.core.resources.di

import dev.nonoxy.d2buildhelper.core.resources.data.local.constants.FileContentReader
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

internal actual fun Module.bindFileContentReader() {
    singleOf(::FileContentReader)
}
