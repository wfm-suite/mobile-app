package org.worklog.app.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrainingCourseRequest(
    @SerialName("course_name") val courseName: String,
    @SerialName("provider") val provider: String? = null,
    @SerialName("completed_date") val completedDate: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null,
    @SerialName("certificate_number") val certificateNumber: String? = null,
    @SerialName("status") val status: String = "completed"
)
