package dev.yashgarg.qbit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.yashgarg.qbit.notifications.AppNotificationManager

@HiltAndroidApp
class QbitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppNotificationManager.createNotificationChannel(applicationContext)
    }
}
