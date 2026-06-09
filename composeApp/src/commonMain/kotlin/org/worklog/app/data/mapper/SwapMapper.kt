package org.worklog.app.data.mapper

import org.worklog.app.data.model.IncomingSwapDto
import org.worklog.app.data.model.SwapRotaSummaryDto
import org.worklog.app.domain.model.IncomingSwap
import org.worklog.app.domain.model.SwapRotaSummary

fun IncomingSwapDto.toDomainModel(): IncomingSwap? {
    val my = myRota?.toDomainModel() ?: return null
    val offered = offeredRota?.toDomainModel() ?: return null
    return IncomingSwap(
        id = id,
        requesterName = requester?.name.orEmpty(),
        requestedAt = requestedAt,
        myRota = my,
        offeredRota = offered
    )
}

fun SwapRotaSummaryDto.toDomainModel(): SwapRotaSummary? {
    val rotaId = id ?: return null
    return SwapRotaSummary(
        rotaId = rotaId,
        date = date,
        shiftStart = shiftStart,
        shiftEnd = shiftEnd,
        shiftType = shiftType,
        branch = branch
    )
}
