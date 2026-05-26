package dev.nonoxy.d2buildhelper.core.network.di

import dev.nonoxy.d2buildhelper.core.network.ktor.di.coreNetworkKtorModule
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformCoreNetworkModule: Module

val coreNetworkModule = module {
    includes(coreNetworkKtorModule, platformCoreNetworkModule)
}
