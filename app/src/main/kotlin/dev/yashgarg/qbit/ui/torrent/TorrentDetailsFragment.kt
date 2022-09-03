package dev.yashgarg.qbit.ui.torrent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentDetailsFragmentBinding
import dev.yashgarg.qbit.ui.torrent.adapter.TorrentDetailsAdapter
import dev.yashgarg.qbit.utils.ClipboardUtil
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import qbittorrent.models.Torrent

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

        binding.toolbar.setNavigationOnClickListener { it.findNavController().navigateUp() }
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

    private fun setupMenu(torrent: Torrent) {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.pause_torrent -> {
                    true
                }
                R.id.resume_torrent -> {
                    true
                }
                R.id.remove_torrent -> {
                    true
                }
                R.id.copy_magnet -> {
                    ClipboardUtil.copyToClipboard(requireContext(), torrent.hash, torrent.magnetUri)
                    true
                }
                else -> false
            }
        }
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
                    setupMenu(torrent)
                }
            }
        }
    }
}
