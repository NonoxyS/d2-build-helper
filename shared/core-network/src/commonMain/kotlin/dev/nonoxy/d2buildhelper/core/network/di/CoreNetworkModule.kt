package dev.nonoxy.d2buildhelper.core.network.di

import dev.nonoxy.d2buildhelper.core.network.ktor.di.coreNetworkKtorModule
import org.koin.dsl.module

val coreNetworkModule = module {
    includes(coreNetworkKtorModule)
}
