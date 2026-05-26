package dev.nonoxy.d2buildhelper.core.network.ktor.certificates

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

@Suppress("CustomX509TrustManager")
internal class TrustAllX509TrustManager : X509TrustManager {

    @Suppress("TrustAllX509TrustManager")
    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
    }

    @Suppress("TrustAllX509TrustManager")
    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
}
