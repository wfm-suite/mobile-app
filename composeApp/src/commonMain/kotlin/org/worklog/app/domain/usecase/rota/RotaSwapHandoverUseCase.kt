package org.worklog.app.domain.usecase.rota

import org.worklog.app.domain.repository.RotaRepository

class RotaSwapHandoverUseCase(
    private val rotaRepository: RotaRepository
) {
    suspend fun rotaHanoverRequest(rotaId: Int) = rotaRepository.rotaHanoverRequest(rotaId)

    suspend fun rotaSwapRequest(myRotaId: Int, requestedRotaId: Int) =
        rotaRepository.rotaSwapRequest(myRotaId, requestedRotaId)

    suspend fun getIncomingSwaps(forceRefresh: Boolean = false) =
        rotaRepository.getIncomingSwaps(forceRefresh)

    suspend fun acceptSwap(swapId: Int) =
        rotaRepository.respondToSwap(swapId, "accept")

    suspend fun denySwap(swapId: Int) =
        rotaRepository.respondToSwap(swapId, "deny")

    suspend fun cancelSwap(swapId: Int) =
        rotaRepository.cancelSwap(swapId)

    suspend fun cancelHandover(handoverId: Int) =
        rotaRepository.cancelHandover(handoverId)
}