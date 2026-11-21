package com.newlauncher.app.notification

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.newlauncher.core.common.Log
import com.newlauncher.feature.dynamicisland.DynamicIslandController
import com.newlauncher.feature.dynamicisland.IslandActivity

/**
 * Optional notification listener that surfaces incoming notifications in the
 * Dynamic Island.
 *
 * This service only runs when the user has explicitly granted notification
 * access from the system Settings screen. Without that grant it is never
 * invoked, so the launcher never silently reads notifications. Notifications
 * are shown on-device only and are never uploaded.
 */
class IslandNotificationListener : NotificationListenerService() {

    private val tag = Log.tag("IslandNotif")

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!isAccessGranted(this)) return
        val notification = sbn.notification ?: return
        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: sbn.packageName
        val text = extras.getString(Notification.EXTRA_TEXT)
        try {
            DynamicIslandController.setActivity(IslandActivity.Notification(title, text))
        } catch (e: Exception) {
            Log.e(tag, "Failed to surface notification", e)
        }
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification,
        reason: Int,
        rank: Int,
        rankChange: Int,
    ) {
        if (!isAccessGranted(this)) return
        // Only clear if there is no other active activity to preserve.
        val current = DynamicIslandController.state.value.activity
        if (current is IslandActivity.Notification &&
            current.title == (sbn.notification.extras.getString(Notification.EXTRA_TITLE) ?: sbn.packageName)
        ) {
            DynamicIslandController.clear()
        }
    }

    companion object {
        /** Whether the user has granted notification-listener access. */
        fun isAccessGranted(context: android.content.Context): Boolean =
            NotificationListenerService.getBindingList(context).isNotEmpty()
    }
}
