package dev.yashgarg.qbit.ui.torrent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentDetailsFragmentBinding
import dev.yashgarg.qbit.ui.torrent.adapter.TorrentDetailsAdapter
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TorrentDetailsFragment : Fragment(R.layout.torrent_details_fragment) {
    private val binding by viewBinding(TorrentDetailsFragmentBinding::bind)
    private val viewModel by viewModels<TorrentDetailsViewModel>()

    private lateinit var torrentInfoAdapter: TorrentDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        torrentInfoAdapter = TorrentDetailsAdapter(this)
        binding.pager.adapter = torrentInfoAdapter

        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
                tab.text =
                    when (position) {
                        0 -> "Info"
                        1 -> "Files"
                        2 -> "Trackers"
                        else -> "Peers"
                    }
            }
            .attach()
        observeFlows()
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(state: TorrentDetailsState) {
        with(binding) {
            if (!state.loading) {
                if (state.error != null) {
                    requireParentFragment().parentFragmentManager.popBackStackImmediate()
                } else {
                    val torrent = requireNotNull(state.torrent)
                    toolbar.title = torrent.name
                }
            }
        }
    }
}
