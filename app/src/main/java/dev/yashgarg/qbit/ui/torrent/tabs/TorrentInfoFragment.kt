package dev.yashgarg.qbit.ui.torrent.tabs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentInfoFragmentBinding
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsState
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsViewModel
import dev.yashgarg.qbit.utils.toDate
import dev.yashgarg.qbit.utils.toHumanReadable
import dev.yashgarg.qbit.utils.toTime
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TorrentInfoFragment : Fragment(R.layout.torrent_info_fragment) {
    private val binding by viewBinding(TorrentInfoFragmentBinding::bind)
    private val viewModel by
        viewModels<TorrentDetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                val torrent = requireNotNull(state.torrent)
                val props = requireNotNull(state.torrentProperties)

                connections.setSubtitle("${props.nbConnections} (${props.nbConnectionsLimit} max)")
                seeds.setSubtitle("${props.seeds} (${props.seedsTotal} total)")
                peers.setSubtitle("${props.peers} (${props.peersTotal} total)")
                timeActive.setSubtitle(props.timeElapsed.toTime().trim())
                eta.setSubtitle(
                    if (props.eta == 8640000.toLong()) "Inf." else props.eta.toTime().trim()
                )
                downloaded.setSubtitle(
                    "${props.totalDownloaded.toHumanReadable()} (${props.totalDownloadedSession.toHumanReadable()} in this session)"
                )
                uploaded.setSubtitle(
                    "${props.totalUploaded.toHumanReadable()} (${props.totalUploadedSession.toHumanReadable()} in this session)"
                )
                downSpeed.setSubtitle(
                    "${props.dlSpeed.toHumanReadable()} (${props.dlSpeedAvg.toHumanReadable()} avg.)"
                )
                upSpeed.setSubtitle(
                    "${props.upSpeed.toHumanReadable()} (${props.upSpeedAvg.toHumanReadable()} avg.)"
                )
                dlLimit.setSubtitle(props.dlLimit.toHumanReadable().trim())
                upLimit.setSubtitle(props.upLimit.toHumanReadable().trim())
                wasted.setSubtitle(props.totalWasted.toHumanReadable().trim())
                ratio.setSubtitle(props.shareRatio.toString().trim())
                reannounce.setSubtitle(
                    if (props.reannounce == 0L) "Inf." else props.reannounce.toTime().trim()
                )
                lastComplete.setSubtitle(props.lastSeen.toDate())
                priority.setSubtitle(torrent.priority.toString().trim())
            }
        }
    }
}
