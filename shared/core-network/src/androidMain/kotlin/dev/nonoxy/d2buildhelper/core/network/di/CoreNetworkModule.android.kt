package dev.nonoxy.d2buildhelper.core.network.di

import dev.nonoxy.d2buildhelper.common.di.AppEnvironmentQualifiers
import dev.nonoxy.d2buildhelper.core.network.ktor.NetworkEnvironment
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual val platformCoreNetworkModule = module {

    factory<NetworkEnvironment> {
        val flavor: String = get(named(AppEnvironmentQualifiers.FLAVOR))

        when (flavor.lowercase()) {
            "prod" -> NetworkEnvironment.Prod
            "dev" -> NetworkEnvironment.Dev
            else -> error("Unknown flavor: $flavor. Add it to platformCoreNetworkModule.")
        }
    }
}
