package org.worklog.app.domain.usecase.rota

import org.worklog.app.domain.repository.RotaRepository

class RotaSwapHandoverUseCase(
    private val rotaRepository: RotaRepository
) {
    suspend fun rotaHanoverRequest(rotaId: Int) = rotaRepository.rotaHanoverRequest(rotaId)

    suspend fun rotaSwapRequest(myRotaId: Int, requestedRotaId: Int) =
        rotaRepository.rotaSwapRequest(myRotaId, requestedRotaId)
}