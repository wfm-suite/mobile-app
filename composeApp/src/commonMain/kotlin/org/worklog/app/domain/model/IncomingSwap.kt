package org.worklog.app.domain.model

data class IncomingSwap(
    val id: Int,
    val requesterName: String,
    val requestedAt: String?,
    val myRota: SwapRotaSummary,
    val offeredRota: SwapRotaSummary
)

data class SwapRotaSummary(
    val rotaId: Int,
    val date: String?,
    val shiftStart: String?,
    val shiftEnd: String?,
    val shiftType: String?,
    val branch: String?
)
