package dev.yashgarg.qbit.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dev.yashgarg.qbit.common.R

object AppNotificationManager {
    fun createNotificationChannel(context: Context) {
        /**
         * Create the [NotificationChannel], but only on API 26+ because the [NotificationChannel]
         * class is new and not in the support library
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    context.getString(R.string.status_channel_id),
                    context.getString(R.string.status_updates),
                    NotificationManager.IMPORTANCE_HIGH
                )

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
