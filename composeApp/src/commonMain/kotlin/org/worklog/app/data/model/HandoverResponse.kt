package org.worklog.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyHandoversResponse(
    val handovers: List<MyHandoverDto> = emptyList()
)

@Serializable
data class MyHandoverDto(
    val id: Int,
    val status: String? = null,
    @SerialName("requested_at")
    val requestedAt: String? = null,
    @SerialName("approved_at")
    val approvedAt: String? = null,
    val note: String? = null,
    val rota: HandoverRotaDto? = null
)

@Serializable
data class HandoverRotaDto(
    val id: Int? = null,
    val date: String? = null,
    @SerialName("shift_start_time")
    val shiftStartTime: String? = null,
    @SerialName("shift_end_time")
    val shiftEndTime: String? = null,
    @SerialName("short_code")
    val shortCode: String? = null
)
