package dev.nonoxy.d2buildhelper.core.network.ktor

import io.ktor.http.URLProtocol

enum class NetworkEnvironment(
    val protocol: URLProtocol,
    val apiHost: String,
) {
    Dev(
        protocol = URLProtocol.HTTPS,
        apiHost = "d2bh-api.nonoxy.dev",
    ),
    Prod(
        protocol = URLProtocol.HTTPS,
        apiHost = "d2bh-api.nonoxy.dev",
    ),
}
