package org.worklog.app.data.source.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.worklog.app.core.util.ResultWrapper

class ApiService(
    val httpClient: HttpClient
) {
    suspend inline fun <reified T> safeApiCall(
        apiCall: suspend () -> HttpResponse
    ): ResultWrapper<T> {
        return try {
            val response = apiCall()

            if (response.status.isSuccess()) {
                val responseBody: T = response.body()
                ResultWrapper.Success(responseBody)
            } else {
                val errorText = response.bodyAsText()
                val fallbackMessage = "Error ${response.status.value}: ${response.status.description}"
                ResultWrapper.Error(
                    message = parseApiErrorMessage(errorText, fallbackMessage),
                    code = response.status.value
                )
            }
        } catch (e: Exception) {
            e.toNetworkError()
        }
    }

    suspend inline fun <reified T> get(
        endpoint: String,
        parameters: Map<String, String> = emptyMap()
    ): ResultWrapper<T> {
        return safeApiCall {
            httpClient.get(endpoint) {

                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }

    suspend inline fun <reified T> post(
        endpoint: String,
        body: Any? = null
    ): ResultWrapper<T> {
        return safeApiCall {
            httpClient.post(endpoint) {

                contentType(ContentType.Application.Json)
                if (body != null) {
                    setBody(body)
                }
            }
        }
    }

    suspend inline fun <reified T> postForm(
        endpoint: String,
        formData: Map<String, String>
    ): ResultWrapper<T> {
        return safeApiCall {
            httpClient.post(endpoint) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    formData.forEach { (key, value) ->
                        append(key, value)
                    }
                }))
            }
        }
    }

    suspend inline fun <reified T> postMultipart(
        endpoint: String,
        imageBytes: ByteArray,
        fileName: String = ""
    ): ResultWrapper<T> {

        return safeApiCall {
            httpClient.submitFormWithBinaryData(
                url = endpoint,
                formData = formData {
                    append(
                        key = "profile_picture",
                        value = imageBytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"profile_picture\"; filename=\"$fileName\""
                            )
                        }
                    )
                }
            ) {

            }
        }
    }

    suspend inline fun <reified T> put(
        endpoint: String,
        body: Any? = null
    ): ResultWrapper<T> {
        return safeApiCall {
            httpClient.put(endpoint) {

                contentType(ContentType.Application.Json)
                if (body != null) {
                    setBody(body)
                }
            }
        }
    }

    suspend inline fun <reified T> delete(
        endpoint: String
    ): ResultWrapper<T> {
        return safeApiCall {
            httpClient.delete(endpoint)
        }
    }
}

private val errorJson = Json { ignoreUnknownKeys = true }

fun parseApiErrorMessage(errorBody: String, fallback: String): String {
    val parsedMessage = runCatching {
        val root = errorJson.parseToJsonElement(errorBody) as? JsonObject ?: return@runCatching null

        val validationMessage = (root["data"] as? JsonObject)
            ?.values
            ?.asSequence()
            ?.mapNotNull { it as? JsonArray }
            ?.flatMap { it.asSequence() }
            ?.firstNotNullOfOrNull { (it as? JsonPrimitive)?.contentOrNull }

        validationMessage ?: (root["message"] as? JsonPrimitive)?.contentOrNull
    }.getOrNull()

    return parsedMessage?.takeIf { it.isNotBlank() } ?: fallback
}

suspend fun Exception.toNetworkError(): ResultWrapper.Error {
    val errorMessage = when (this) {
        is ClientRequestException -> {
            val errorText = runCatching { this.response.bodyAsText() }.getOrDefault("")
            val fallbackMessage = if (this.response.status == HttpStatusCode.NotFound) {
                "Oops! The requested page could not be found. Please try again later."
            } else {
                "Client Error: ${this.response.status.description}"
            }
            parseApiErrorMessage(errorText, fallbackMessage)
        }

        is ServerResponseException -> {
            val errorText = runCatching { this.response.bodyAsText() }.getOrDefault("")
            parseApiErrorMessage(errorText, "Server Error: ${this.response.status.description}")
        }
        is TimeoutCancellationException -> "Request Timed Out"
        else -> this.message ?: "Unknown Error"
    }
    val statusCode = when (this) {
        is ClientRequestException -> this.response.status.value
        is ServerResponseException -> this.response.status.value
        else -> null
    }
    return ResultWrapper.Error(errorMessage, statusCode)
}