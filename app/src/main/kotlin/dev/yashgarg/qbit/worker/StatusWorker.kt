package dev.yashgarg.qbit.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat.Action
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.yashgarg.qbit.MainActivity
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.notifications.AppNotificationManager
import dev.yashgarg.qbit.utils.toHumanReadable

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
        val closeIntent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val intent =
            Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        client?.observeMainData()?.collect { data ->
            val state = data.serverState
            setForeground(
                ForegroundInfo(
                    1,
                    AppNotificationManager.createNotification(
                        applicationContext,
                        "Server State • Connected",
                        "DL: ${state.dlInfoSpeed.toHumanReadable()}/s | UL: ${state.upInfoSpeed.toHumanReadable()}/s",
                        R.drawable.baseline_sync,
                        true,
                        listOf(Action(null, "Close", closeIntent)),
                        pendingIntent
                    )
                )
            )
        }
    }

    companion object {
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    }
}
