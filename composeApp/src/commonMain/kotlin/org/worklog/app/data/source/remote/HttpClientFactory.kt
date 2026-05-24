package org.worklog.app.data.source.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import org.worklog.app.data.provider.AuthTokenProvider

expect fun createHttpClient(authTokenProvider: AuthTokenProvider): HttpClient

private val noAuthPaths = listOf(
    "/login",
    "/forgot-password",
    "/reset-password",
    "/auth/otp/send",
    "/auth/otp/resend",
    "/auth/otp/verify"
)

class BearerTokenPlugin private constructor(val provider: AuthTokenProvider) {

    class Config {
        lateinit var provider: AuthTokenProvider
    }

    companion object Plugin : HttpClientPlugin<Config, BearerTokenPlugin> {
        override val key: AttributeKey<BearerTokenPlugin> = AttributeKey("BearerToken")

        override fun prepare(block: Config.() -> Unit): BearerTokenPlugin {
            return BearerTokenPlugin(Config().apply(block).provider)
        }

        override fun install(plugin: BearerTokenPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                val url = context.url.buildString()
                val isAuthEndpoint = noAuthPaths.any { url.contains(it) }
                
                if (isAuthEndpoint) {
                    context.headers.remove(HttpHeaders.Authorization)
                } else {
                    val token = plugin.provider.getTokenOrEmpty()
                    if (token.isNotEmpty()) {
                        val authHeaderValue = if (token.startsWith("Bearer ", ignoreCase = true)) {
                            token
                        } else {
                            "Bearer $token"
                        }
                        context.headers.set(HttpHeaders.Authorization, authHeaderValue)
                    }
                }
                proceed()
            }
        }
    }
}

fun HttpClientConfig<*>.createBaseHttpClientConfig(
    authTokenProvider: AuthTokenProvider
) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 90000L
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                println(message)
            }
        }
    }
    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header(HttpHeaders.Accept, ContentType.Application.Json)
    }
    install(BearerTokenPlugin) {
        provider = authTokenProvider
    }
}
