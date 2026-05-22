package dev.nonoxy.d2buildhelper.core.network.ktor

import io.ktor.http.URLProtocol

enum class NetworkEnvironment(
    val protocol: URLProtocol,
    val apiHost: String,
    val apiPort: Int?,
) {
    Dev(
        protocol = URLProtocol.HTTP,
        apiHost = "localhost",
        apiPort = 8080,
    ),
    Prod(
        protocol = URLProtocol.HTTPS,
        apiHost = "d2bh-api.nonoxy.dev",
        apiPort = null,
    ),
}
