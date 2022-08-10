package dev.yashgarg.qbit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QbitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
