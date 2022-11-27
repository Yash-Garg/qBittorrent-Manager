package dev.yashgarg.qbit.ui.server.manager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.ServerManagerFragmentBinding
import dev.yashgarg.qbit.ui.server.adapter.ServerConfigAdapter
import dev.yashgarg.qbit.utils.viewBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ServerManagerFragment : Fragment(R.layout.server_manager_fragment) {
    private val binding by viewBinding(ServerManagerFragmentBinding::bind)
    private val viewModel by viewModels<ServerManagerViewModel>()

    @Inject lateinit var serverConfigAdapter: ServerConfigAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            serverRv.adapter = serverConfigAdapter
            addServerFab.setOnClickListener {
                findNavController().navigate(R.id.action_serverManagerFragment_to_configFragment)
            }
        }
        observeFlows()
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(state: ServerManagerState) {
        with(state) {
            if (!configsLoading && !error && configs.isNotEmpty()) {
                serverConfigAdapter.configs = configs
            }
        }
    }
}
