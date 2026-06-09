package org.worklog.app.domain.model

data class UserResignationRequest(
    val id: Int,
    val lastWorkingDay: String,
    val reason: String? = null,
    val status: String = "pending",
    val submittedAt: String
)
