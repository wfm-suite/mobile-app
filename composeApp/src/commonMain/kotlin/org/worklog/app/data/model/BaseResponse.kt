package org.worklog.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
)