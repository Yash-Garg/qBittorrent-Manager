package dev.yashgarg.qbit

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.data.models.ConfigStatus
import dev.yashgarg.qbit.databinding.ActivityMainBinding
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

        lifecycleScope.launch {
            clientManager.configStatus.collect { status ->
                when (status) {
                    ConfigStatus.EXISTS -> {
                        findNavController(this@MainActivity, R.id.nav_host_fragment)
                            .navigate(R.id.action_homeFragment_to_serverFragment)
                    }
                    ConfigStatus.DOES_NOT_EXIST -> {}
                }
            }
        }
    }
}
