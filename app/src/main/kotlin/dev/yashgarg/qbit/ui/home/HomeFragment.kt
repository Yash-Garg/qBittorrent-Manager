package dev.yashgarg.qbit.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.data.models.ConfigStatus
import dev.yashgarg.qbit.databinding.HomeFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {
    private val binding by viewBinding(HomeFragmentBinding::bind)

    @Inject lateinit var clientManager: ClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        viewLifecycleOwner.lifecycleScope.launch {
            clientManager.configStatus.collect { status ->
                if (status == ConfigStatus.EXISTS) {
                    // TODO: Just for testing purposes, remove this afterwards
                    findNavController().navigate(R.id.action_homeFragment_to_serverFragment)
                }
            }
        }

        binding.addServerFab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_configFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).setSupportActionBar(null)
    }
}
