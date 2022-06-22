package dev.yashgarg.qbit

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QbitApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
