package org.worklog.app.data.source.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.worklog.app.data.model.BaseResponse
import org.worklog.app.data.model.RefreshTokenResponse
import org.worklog.app.data.provider.AuthTokenProvider

expect fun createHttpClient(authTokenProvider: AuthTokenProvider): HttpClient

private val noAuthPaths = listOf(
    "/login",
    "/forgot-password",
    "/reset-password",
    "/auth/otp/send",
    "/auth/otp/resend",
    "/auth/otp/verify",
    "/auth/refresh"
)

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

    install(Auth) {
        bearer {
            loadTokens {
                val token = authTokenProvider.getTokenOrEmpty()
                val refresh = authTokenProvider.getRefreshTokenOrEmpty()
                println("Auth: Loading tokens - Access: ${token.take(10)}..., Refresh: ${refresh.take(10)}...")
                if (token.isNotEmpty()) {
                    BearerTokens(token, refresh)
                } else null
            }

            refreshTokens {
                val refreshToken = authTokenProvider.getRefreshTokenOrEmpty()
                println("Auth: Refreshing tokens using: ${refreshToken.take(10)}...")
                if (refreshToken.isEmpty()) {
                    println("Auth: No refresh token available")
                    return@refreshTokens null
                }

                try {
                    val refreshResponse = client.post("https://mobile-api.gbspares.com/api/app/auth/refresh") {
                        setBody(mapOf("refresh_token" to refreshToken))
                        header(HttpHeaders.Authorization, null)
                    }

                    println("Auth: Refresh response status: ${refreshResponse.status}")

                    if (refreshResponse.status.isSuccess()) {
                        val baseResponse = refreshResponse.body<BaseResponse<RefreshTokenResponse>>()
                        val newAccess = baseResponse.data?.accessToken
                        val newRefresh = baseResponse.data?.refreshToken ?: refreshToken
                        if (newAccess != null) {
                            println("Auth: Token refresh successful")
                            authTokenProvider.setTokens(newAccess, newRefresh)
                            BearerTokens(newAccess, newRefresh)
                        } else {
                            println("Auth: Token refresh failed - Access token is null")
                            authTokenProvider.emitForcedLogout()
                            null
                        }
                    } else {
                        println("Auth: Token refresh failed - HTTP ${refreshResponse.status}")
                        authTokenProvider.emitForcedLogout()
                        null
                    }
                } catch (e: Exception) {
                    println("Auth: Token refresh failed with exception: ${e.message}")
                    authTokenProvider.emitForcedLogout()
                    null
                }
            }

            // Return true to attach the bearer preemptively (default behavior for
            // authenticated endpoints). Return false for auth endpoints (login,
            // refresh, otp, reset) so the stale token isn't carried along.
            sendWithoutRequest { request ->
                val isNoAuth = noAuthPaths.any { path -> request.url.toString().contains(path) }
                !isNoAuth
            }
        }
    }
}
