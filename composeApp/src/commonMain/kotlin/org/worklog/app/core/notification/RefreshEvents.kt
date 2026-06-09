package org.worklog.app.core.notification

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Lightweight in-process pub/sub used to nudge active ViewModels to refresh
 * when an FCM push arrives. Topics are coarse-grained strings so they survive
 * adding new event types without changing this class.
 *
 * Typical wiring:
 *  - WorkLogFirebaseMessagingService.onMessageReceived reads `data["type"]`
 *    and calls [emit] with the matching [Topics] string.
 *  - ViewModels collect [events] in their init block and call their own
 *    `refreshData(forceRefresh = true)` when a relevant topic fires.
 *
 * Replay = 0: no missed-event catch-up. ViewModels already do a fresh fetch
 * on screen-resume via LifecycleResumeEffect, so replay would just double-load.
 * Extra buffer = 8 so bursts of events near-simultaneously aren't dropped.
 */
class RefreshEvents {
    private val _events = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 8)
    val events: SharedFlow<String> = _events.asSharedFlow()

    fun emit(topic: String) {
        _events.tryEmit(topic)
    }

    object Topics {
        const val LEAVES = "leaves"
        const val ROTAS = "rotas"
        const val HANDOVERS = "handovers"
        const val SWAPS = "swaps"
        const val NOTIFICATIONS = "notifications"
    }

    /**
     * Map an FCM `data.type` string to the topics that should be emitted.
     * Returns an empty set for unknown types — the system notification still
     * shows, but no refresh is triggered.
     */
    companion object {
        fun topicsFor(type: String?): Set<String> = when (type?.lowercase()) {
            // Holiday approved also rewrites the rota days to short_code='AL'
            // server-side, so Home and My Team need to refresh too.
            "holiday_approved", "holiday_rejected", "leave", "holiday_requested" ->
                setOf(Topics.LEAVES, Topics.ROTAS, Topics.NOTIFICATIONS)
            "handover_accepted", "handover_rejected", "handover", "handover_requested" ->
                setOf(Topics.ROTAS, Topics.HANDOVERS, Topics.NOTIFICATIONS)
            "swap_accepted", "swap_rejected", "swap", "swap_requested" ->
                setOf(Topics.ROTAS, Topics.SWAPS, Topics.NOTIFICATIONS)
            "rota_updated", "rota_changed", "rota" ->
                setOf(Topics.ROTAS, Topics.NOTIFICATIONS)
            null, "" -> emptySet()
            else -> setOf(Topics.NOTIFICATIONS)
        }
    }
}
