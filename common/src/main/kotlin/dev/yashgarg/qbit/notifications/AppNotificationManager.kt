package dev.yashgarg.qbit.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        } else true
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermission(context: Context, permissionLauncher: ActivityResultLauncher<String>) {
        val hasPermission = checkPermission(context)

        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun createNotification(
        context: Context,
        title: String,
        content: String,
        @DrawableRes smallIcon: Int,
        persistent: Boolean = false,
    ): Notification {
        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.status_channel_id))
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(!persistent)
                .setOngoing(persistent)
                .setOnlyAlertOnce(persistent)

        return builder.build()
    }

    // We already check for permissions later in the process, so we can suppress this lint
    @SuppressLint("MissingPermission")
    fun sendNotification(context: Context, notificationId: Int, notification: Notification) {
        val sendNotif =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermission(context)
            } else {
                true
            }

        if (sendNotif) {
            updateNotification(context, notificationId, notification)
        }
    }

    @SuppressLint("MissingPermission")
    fun updateNotification(context: Context, notificationId: Int, notification: Notification) {
        with(NotificationManagerCompat.from(context)) { notify(notificationId, notification) }
    }
}
