package dev.yashgarg.qbit.ui.server

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.ServerFragmentBinding
import dev.yashgarg.qbit.ui.dialogs.AddTorrentDialog
import dev.yashgarg.qbit.ui.server.adapter.TorrentListAdapter
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ServerFragment : Fragment(R.layout.server_fragment) {
    private val binding by viewBinding(ServerFragmentBinding::bind)
    private val viewModel by viewModels<ServerViewModel>()
    private var torrentListAdapter = TorrentListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHandlers()
        observeFlows()
    }

    private fun setupHandlers() {
        with(binding) {
            torrentRv.adapter = torrentListAdapter
            addTorrentFab.setOnClickListener {
                AddTorrentDialog.newInstance().show(childFragmentManager, AddTorrentDialog.TAG)
            }
        }
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(state: ServerState) {
        with(binding) {
            if (state.hasError) {
                listLoader.visibility = View.GONE
                emptyTv.text = state.error?.message ?: requireContext().getString(R.string.error)
                emptyTv.visibility = View.VISIBLE
                torrentRv.visibility = View.GONE
            }

            if (!state.dataLoading) {
                listLoader.visibility = View.GONE
                if (state.data!!.torrents.isEmpty()) {
                    emptyTv.visibility = View.VISIBLE
                    torrentRv.visibility = View.GONE
                } else {
                    emptyTv.visibility = View.GONE
                    torrentRv.apply {
                        visibility = View.VISIBLE
                        torrentListAdapter.setData(state.data.torrents)
                    }
                }
            }
        }
    }
}
