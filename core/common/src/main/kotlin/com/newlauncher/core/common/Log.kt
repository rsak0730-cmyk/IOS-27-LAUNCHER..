package com.newlauncher.core.common

/**
 * Thin logging wrapper so the tag is centralized and log statements stay
 * consistent across modules. Modules call [Log.tag] with their own suffix.
 *
 * The platform logger is referenced fully-qualified to avoid clashing with this
 * object's own name.
 */
object Log {
    private const val BASE_TAG = "NewLauncher"

    /** Build a consistent tag, e.g. `NewLauncher.GlassEngine`. */
    fun tag(suffix: String): String = "$BASE_TAG.$suffix"

    fun d(tag: String, message: String) {
        android.util.Log.d(tag, message)
    }

    fun i(tag: String, message: String) {
        android.util.Log.i(tag, message)
    }

    fun w(tag: String, message: String) {
        android.util.Log.w(tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            android.util.Log.e(tag, message, throwable)
        } else {
            android.util.Log.e(tag, message)
        }
    }
}
