package dev.nonoxy.d2buildhelper.core.network.di

import dev.nonoxy.d2buildhelper.core.network.ktor.NetworkEnvironment
import org.koin.core.component.KoinComponent

class NetworkEnvironmentDi : KoinComponent {

    fun insertKoin(environment: NetworkEnvironment) {
        getKoin().declare<NetworkEnvironment>(environment)
    }
}
