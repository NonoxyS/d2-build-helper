package dev.nonoxy.d2buildhelper.core.network.ktor.certificates

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import java.security.SecureRandom
import javax.net.ssl.SSLContext

internal actual fun HttpClientConfig<*>.configureCertificates() {
    engine {
        this as OkHttpConfig

        config {
            val trustAllCert = TrustAllX509TrustManager()
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustAllCert), SecureRandom())
            sslSocketFactory(sslContext.socketFactory, trustAllCert)
        }
    }
}
