package org.worklog.app.data.source.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.worklog.app.data.provider.AuthTokenProvider

actual fun createHttpClient(authTokenProvider: AuthTokenProvider): HttpClient {
    return HttpClient(CIO) {
        createBaseHttpClientConfig(authTokenProvider)
    }
}