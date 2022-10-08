package dev.yashgarg.qbit.utils

import android.content.Context
import androidx.core.app.NotificationCompat.Action
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.notifications.AppNotificationManager

@HiltWorker
class StatusWorker
@AssistedInject
constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val clientManager: ClientManager,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        getStatus()
        return Result.success()
    }

    private suspend fun getStatus() {
        val client = clientManager.checkAndGetClient()
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        client?.observeMainData()?.collect { data ->
            val state = data.serverState
            setForeground(
                ForegroundInfo(
                    1,
                    AppNotificationManager.createNotification(
                        applicationContext,
                        "Server State: Connected",
                        "DL: ${state.dlInfoSpeed.toHumanReadable()} | UL: ${state.upInfoSpeed.toHumanReadable()}",
                        R.drawable.baseline_sync,
                        true,
                        listOf(Action(null, "Close", intent))
                    )
                )
            )
        }
    }
}
