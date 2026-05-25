package dev.nonoxy.d2buildhelper.core.storage.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformCoreStorageModule: Module

val coreStorageModule = module {
    includes(platformCoreStorageModule)
}
