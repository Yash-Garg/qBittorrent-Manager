package dev.yashgarg.qbit

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.data.models.ConfigStatus
import dev.yashgarg.qbit.databinding.ActivityMainBinding
import dev.yashgarg.qbit.notifications.AppNotificationManager
import dev.yashgarg.qbit.utils.StatusWorker
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var clientManager: ClientManager

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(applicationContext)
        }

        lifecycleScope.launch {
            clientManager.configStatus.collect { status ->
                when (status) {
                    ConfigStatus.EXISTS -> {
                        val constraints =
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()

                        WorkManager.getInstance(applicationContext)
                            .enqueueUniqueWork(
                                "status_update",
                                ExistingWorkPolicy.REPLACE,
                                OneTimeWorkRequestBuilder<StatusWorker>()
                                    .setConstraints(constraints)
                                    .build()
                            )

                        findNavController(this@MainActivity, R.id.nav_host_fragment)
                            .navigate(R.id.action_homeFragment_to_serverFragment)
                    }
                    ConfigStatus.DOES_NOT_EXIST -> Log.i(ClientManager.tag, "No config found!")
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
}
