package com.newlauncher.core.common

import android.util.Log

/**
 * Thin logging wrapper so the tag is centralized and log statements stay
 * consistent across modules. Modules call [Log.tag] with their own suffix.
 */
object Log {
    private const val BASE_TAG = "NewLauncher"

    /** Build a consistent tag, e.g. `NewLauncher.GlassEngine`. */
    fun tag(suffix: String): String = "$BASE_TAG.$suffix"

    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}
