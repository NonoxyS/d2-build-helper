package dev.nonoxy.d2buildhelper.core.network.ktor.certificates

import io.ktor.client.HttpClientConfig

internal expect fun HttpClientConfig<*>.configureCertificates()
