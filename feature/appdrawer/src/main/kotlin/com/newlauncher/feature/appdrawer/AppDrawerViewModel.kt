package com.newlauncher.feature.appdrawer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.newlauncher.core.common.AppInfo
import com.newlauncher.core.common.AppProvider
import com.newlauncher.core.common.AppSorter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the app drawer.
 *
 * Loads the launchable applications once (and on refresh), exposes the full
 * alphabetically-sorted list and a search query, and derives the filtered,
 * sectioned view consumed by the UI. Launching delegates to [AppProvider].
 */
class AppDrawerViewModel(app: Application) : AndroidViewModel(app) {

    private val provider = AppProvider(app)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    /** All launchable apps, sorted alphabetically. */
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _query = MutableStateFlow("")
    /** Current search query. */
    val query: StateFlow<String> = _query.asStateFlow()

    /** Filtered + sorted apps matching the current query. */
    val visibleApps: StateFlow<List<AppInfo>> =
        combine(_apps, _query) { all, q -> AppSorter.search(all, q) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Sectioned view (by first letter) of the visible apps. */
    val sections: StateFlow<Map<String, List<AppInfo>>> =
        visibleApps
            .map { AppSorter.sectionByLetter(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    init {
        refresh()
    }

    /** Reload the installed-application list. */
    fun refresh() {
        viewModelScope.launch { _apps.value = provider.getLaunchableApps() }
    }

    /** Update the search query. */
    fun setQuery(q: String) {
        _query.value = q
    }

    /** Launch an app by its package + class. */
    fun launch(app: AppInfo): Boolean = provider.launch(app)
}
