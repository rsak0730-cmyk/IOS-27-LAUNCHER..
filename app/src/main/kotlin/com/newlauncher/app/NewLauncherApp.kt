package com.newlauncher.app

import android.app.Application

/**
 * The NEW-LAUNCHER application.
 *
 * Creates the [AppContainer] once so every component shares the same glass
 * engine, quality manager and persistence layer. No background services are
 * started here; the launcher does not run anything in the background unless a
 * user-triggered feature (e.g. the notification listener, when granted) needs
 * it.
 */
class NewLauncherApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
