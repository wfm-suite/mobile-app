package org.worklog.app.data.util

import org.worklog.app.core.util.ResultWrapper
import org.worklog.app.data.model.BaseResponse

suspend fun <ApiResponse, Domain> handleApiResponse(
    call: suspend () -> ResultWrapper<BaseResponse<ApiResponse>>,
    mapper: (ApiResponse) -> Domain?
): ResultWrapper<Domain> {
    return when (val result = call()) {
        is ResultWrapper.Success -> {
            val body = result.data.data
            val mapped = body?.let { mapper(it) }

            if (mapped != null) {
                ResultWrapper.Success(mapped)
            } else {
                ResultWrapper.Error("Invalid or missing data in response")
            }
        }

        is ResultWrapper.Error -> result
        is ResultWrapper.Loading -> result
    }
}

suspend fun <ApiResponse, Domain> handleNullableApiResponse(
    call: suspend () -> ResultWrapper<BaseResponse<ApiResponse>>,
    mapper: (ApiResponse) -> Domain?
): ResultWrapper<Domain?> {
    return when (val result = call()) {
        is ResultWrapper.Success -> {
            val body = result.data.data
            val mapped = body?.let { mapper(it) }
            ResultWrapper.Success(mapped)
        }

        is ResultWrapper.Error -> result
        is ResultWrapper.Loading -> result
    }
}

suspend fun <T> handleSuccessResponse(
    call: suspend () -> ResultWrapper<BaseResponse<T>>
): ResultWrapper<String> {
    return when (val result = call()) {
        is ResultWrapper.Success -> ResultWrapper.Success(result.data.message)
        is ResultWrapper.Error -> result
        is ResultWrapper.Loading -> ResultWrapper.Loading
    }
}