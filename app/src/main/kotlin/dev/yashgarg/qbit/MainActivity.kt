package dev.yashgarg.qbit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.navigation.Navigation.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.data.models.ConfigStatus
import dev.yashgarg.qbit.data.models.ServerPreferences
import dev.yashgarg.qbit.databinding.ActivityMainBinding
import dev.yashgarg.qbit.notifications.AppNotificationManager
import dev.yashgarg.qbit.worker.StatusWorker
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var clientManager: ClientManager
    @Inject lateinit var serverPrefsStore: DataStore<ServerPreferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(applicationContext)
        }

        lifecycleScope.launch {
            whenResumed {
                clientManager.configStatus.collect { status ->
                    when (status) {
                        ConfigStatus.EXISTS -> {
                            val bundle = bundleOf(TORRENT_INTENT_KEY to intent?.data.toString())
                            val navController =
                                findNavController(this@MainActivity, R.id.nav_host_fragment)

                            serverPrefsStore.data
                                .map { it.showNotification }
                                .onEach(::launchWorkManager)
                                .launchIn(lifecycleScope)

                            if (navController.currentDestination?.id == R.id.homeFragment) {
                                navController.navigate(
                                    R.id.action_homeFragment_to_serverFragment,
                                    bundle
                                )
                            }
                        }
                        ConfigStatus.DOES_NOT_EXIST -> Log.i(ClientManager.tag, "No config found!")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions(context: Context) {
        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                Log.i(AppNotificationManager.javaClass.simpleName, "Notification permission: $it")
            }

        AppNotificationManager.requestPermission(context, permissionLauncher)
    }

    private fun launchWorkManager(show: Boolean) {
        val workTag = "status_update"
        val workManager = WorkManager.getInstance(applicationContext)
        if (show && AppNotificationManager.checkPermission(applicationContext)) {
            workManager.enqueueUniqueWork(
                workTag,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<StatusWorker>()
                    .setConstraints(StatusWorker.constraints)
                    .build()
            )
        } else {
            workManager.cancelAllWorkByTag(workTag)
        }
    }

    companion object {
        const val TORRENT_INTENT_KEY = "torrent_intent"
    }
}
