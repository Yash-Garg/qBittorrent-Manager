package dev.yashgarg.qbit.ui.torrent

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentDetailsFragmentBinding
import dev.yashgarg.qbit.ui.dialogs.RemoveTorrentDialog
import dev.yashgarg.qbit.ui.dialogs.RenameTorrentDialog
import dev.yashgarg.qbit.ui.torrent.adapter.TorrentDetailsAdapter
import dev.yashgarg.qbit.utils.ClipboardUtil
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.saket.cascade.CascadePopupMenu
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
        setupDialogListeners(torrent)
        binding.toolbar.findViewById<View>(R.id.overflow_menu).setOnClickListener { view ->
            val popupMenu = CascadePopupMenu(requireContext(), view)
            popupMenu.menu.apply {
                add("Pause").setIcon(R.drawable.twotone_pause_24).setOnMenuItemClickListener {
                    viewModel.toggleTorrent(true, torrent.hash)
                    Toast.makeText(requireContext(), "Paused", Toast.LENGTH_SHORT).show()
                    true
                }
                add("Resume").setIcon(R.drawable.twotone_play_arrow_24).setOnMenuItemClickListener {
                    viewModel.toggleTorrent(false, torrent.hash)
                    Toast.makeText(requireContext(), "Resumed", Toast.LENGTH_SHORT).show()
                    true
                }
                add("Delete").setIcon(R.drawable.twotone_delete_24).setOnMenuItemClickListener {
                    RemoveTorrentDialog.newInstance()
                        .show(childFragmentManager, RemoveTorrentDialog.TAG)
                    true
                }
                addSubMenu("Copy").also {
                    it.setIcon(R.drawable.twotone_content_copy_24)
                    it.add("Name").setOnMenuItemClickListener {
                        ClipboardUtil.copyToClipboard(
                            requireContext(),
                            "torrent-name",
                            torrent.name
                        )
                        true
                    }
                    it.add("Info hash").setOnMenuItemClickListener {
                        ClipboardUtil.copyToClipboard(
                            requireContext(),
                            "torrent-hash",
                            torrent.hash
                        )
                        true
                    }
                    it.add("Magnet link").setOnMenuItemClickListener {
                        ClipboardUtil.copyToClipboard(
                            requireContext(),
                            "torrent-magnet",
                            torrent.magnetUri
                        )
                        true
                    }
                }
                add("Force recheck")
                    .setIcon(R.drawable.twotone_find_in_page_24)
                    .setOnMenuItemClickListener {
                        viewModel.forceRecheck(torrent.hash)
                        true
                    }
                add("Force reannounce")
                    .setIcon(R.drawable.twotone_restore_page_24)
                    .setOnMenuItemClickListener {
                        viewModel.forceReannounce(torrent.hash)
                        true
                    }
                add("Rename")
                    .setIcon(R.drawable.twotone_drive_file_rename_outline_24)
                    .setOnMenuItemClickListener {
                        RenameTorrentDialog.newInstance(torrent.name)
                            .show(childFragmentManager, RenameTorrentDialog.TAG)
                        true
                    }
            }
            popupMenu.show()
        }
    }

    private fun setupDialogListeners(torrent: Torrent) {
        childFragmentManager.apply {
            setFragmentResultListener(RemoveTorrentDialog.REMOVE_TORRENT_KEY, viewLifecycleOwner) {
                _,
                bundle ->
                val deleteFiles = bundle.getBoolean(RemoveTorrentDialog.TORRENT_KEY)
                viewModel.removeTorrent(torrent.hash, deleteFiles)
                Toast.makeText(requireContext(), "Removed torrent", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }

            setFragmentResultListener(RenameTorrentDialog.RENAME_TORRENT_KEY, viewLifecycleOwner) {
                _,
                bundle ->
                val torrentName = bundle.getString(RenameTorrentDialog.RENAME_KEY)
                viewModel.renameTorrent(requireNotNull(torrentName), torrent.hash)
                Toast.makeText(requireContext(), "Renamed torrent", Toast.LENGTH_SHORT).show()
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
                    findNavController().navigateUp()
                } else {
                    val torrent = requireNotNull(state.torrent)
                    toolbar.title = torrent.name
                    setupMenu(torrent)
                }
            }
        }
    }
}
