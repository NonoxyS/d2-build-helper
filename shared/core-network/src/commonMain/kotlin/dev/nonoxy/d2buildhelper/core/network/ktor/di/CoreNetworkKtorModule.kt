package dev.nonoxy.d2buildhelper.core.network.ktor.di

import dev.nonoxy.d2buildhelper.core.domain.exception.NetworkUnavailableException
import dev.nonoxy.d2buildhelper.core.network.BuildConfig
import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClient
import dev.nonoxy.d2buildhelper.core.network.ktor.KtorClientImpl
import dev.nonoxy.d2buildhelper.core.network.ktor.NetworkEnvironment
import dev.nonoxy.d2buildhelper.core.network.ktor.certificates.configureCertificates
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.new
import org.koin.dsl.module

private const val API_KEY_HEADER = "X-Api-Key"

val coreNetworkKtorModule = module {

    single<Json> {
        Json { ignoreUnknownKeys = true }
    }

    single<HttpClient> {
        val environment: NetworkEnvironment = get()
        val json: Json = get()
        HttpClient {
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(tag = "HTTP", message = message)
                    }
                }
                sanitizeHeader { header -> header.equals(API_KEY_HEADER, ignoreCase = true) }
            }
            install(ContentNegotiation) {
                json(json)
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { throwable, _ ->
                    when (throwable) {
                        is IOException -> throw NetworkUnavailableException(throwable.message.orEmpty())
                        else -> throw throwable
                    }
                }
            }
            defaultRequest {
                url.protocol = environment.protocol
                url.host = environment.apiHost
                header(API_KEY_HEADER, BuildConfig.D2BH_API_KEY)
            }
            if (environment == NetworkEnvironment.Dev) {
                configureCertificates()
            }
        }
    }

    factory<KtorClient> { new(::KtorClientImpl) }
}
