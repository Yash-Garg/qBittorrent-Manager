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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.navigation.Navigation.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.data.models.ConfigStatus
import dev.yashgarg.qbit.databinding.ActivityMainBinding
import dev.yashgarg.qbit.notifications.AppNotificationManager
import dev.yashgarg.qbit.worker.StatusWorker
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var clientManager: ClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermissions(applicationContext)
            }

            lifecycleScope.launch {
                lifecycle.whenResumed {
                    clientManager.configStatus.collect { status ->
                        when (status) {
                            ConfigStatus.EXISTS -> {
                                if (AppNotificationManager.checkPermission(applicationContext)) {
                                    WorkManager.getInstance(applicationContext)
                                        .enqueueUniqueWork(
                                            "status_update",
                                            ExistingWorkPolicy.REPLACE,
                                            OneTimeWorkRequestBuilder<StatusWorker>()
                                                .setConstraints(StatusWorker.constraints)
                                                .build()
                                        )
                                }

                                val bundle = bundleOf(TORRENT_INTENT_KEY to intent?.data.toString())
                                findNavController(this@MainActivity, R.id.nav_host_fragment)
                                    .navigate(R.id.action_homeFragment_to_serverFragment, bundle)
                            }
                            ConfigStatus.DOES_NOT_EXIST ->
                                Log.i(ClientManager.tag, "No config found!")
                        }
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

    companion object {
        const val TORRENT_INTENT_KEY = "torrent_intent"
    }
}
