package com.newlauncher.feature.dynamicisland

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide controller for the Dynamic Island state.
 *
 * A singleton so that background components (e.g. the notification listener
 * service) and the UI share one source of truth. The UI observes [state];
 * background services call [setActivity]/[clear] to surface or dismiss ongoing
 * activities.
 */
object DynamicIslandController {

    private val _state = MutableStateFlow(IslandState())
    val state: StateFlow<IslandState> = _state.asStateFlow()

    /** Expand the island. */
    fun expand() {
        _state.value = _state.value.copy(expanded = true)
    }

    /** Collapse the island to its compact form. */
    fun collapse() {
        _state.value = _state.value.copy(expanded = false)
    }

    /** Toggle expand/collapse. */
    fun toggle() {
        _state.value = _state.value.copy(expanded = !_state.value.expanded)
    }

    /** Set the active activity (music, timer, notification). */
    fun setActivity(activity: IslandActivity) {
        _state.value = _state.value.copy(activity = activity, expanded = true)
    }

    /** Clear the active activity and collapse. */
    fun clear() {
        _state.value = IslandState()
    }
}
