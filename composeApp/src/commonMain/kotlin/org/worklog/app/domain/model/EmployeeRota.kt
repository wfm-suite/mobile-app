package org.worklog.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeRota(
    val employee: EmployeeInfo,
    val rota: Rota
)
