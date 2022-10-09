package dev.yashgarg.qbit

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.yashgarg.qbit.notifications.AppNotificationManager
import javax.inject.Inject

@HiltAndroidApp
class QbitApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        AppNotificationManager.createNotificationChannel(applicationContext)
    }
}
