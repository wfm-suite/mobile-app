package org.worklog.app.domain.model

data class UserTrainingCourse(
    val id: Int,
    val courseName: String,
    val provider: String? = null,
    val completedDate: String? = null,
    val expiryDate: String? = null,
    val certificateNumber: String? = null,
    val status: String = "completed" // completed, in_progress, planned
)
