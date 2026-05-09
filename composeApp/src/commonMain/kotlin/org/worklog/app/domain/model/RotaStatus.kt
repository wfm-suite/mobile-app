package org.worklog.app.domain.model

enum class RotaStatus(
    val status: String
) {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    NOTHING("")
}