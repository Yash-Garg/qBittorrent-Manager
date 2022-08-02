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

                connections.subtitle =
                    getString(R.string.connections_sub)
                        .format(props.nbConnections, props.nbConnectionsLimit)

                seeds.subtitle = getString(R.string.sp_sub).format(props.seeds, props.seedsTotal)
                peers.subtitle = getString(R.string.sp_sub).format(props.peers, props.peersTotal)

                eta.subtitle =
                    if (props.eta == 8640000L) getString(R.string.infinite) else props.eta.toTime()
                reannounce.subtitle =
                    if (props.reannounce == 0L) getString(R.string.infinite)
                    else props.reannounce.toTime()

                downloaded.subtitle =
                    getString(R.string.dl_up_sub)
                        .format(
                            props.totalDownloaded.toHumanReadable(),
                            props.totalDownloadedSession.toHumanReadable(),
                        )

                uploaded.subtitle =
                    getString(R.string.dl_up_sub)
                        .format(
                            props.totalUploaded.toHumanReadable(),
                            props.totalUploadedSession.toHumanReadable(),
                        )

                downSpeed.subtitle =
                    getString(R.string.dl_up_speed_sub)
                        .format(props.dlSpeed.toHumanReadable(), props.dlSpeedAvg.toHumanReadable())

                upSpeed.subtitle =
                    getString(R.string.dl_up_speed_sub)
                        .format(props.upSpeed.toHumanReadable(), props.upSpeedAvg.toHumanReadable())

                dlLimit.subtitle = props.dlLimit.toHumanReadable()
                upLimit.subtitle = props.upLimit.toHumanReadable()
                wasted.subtitle = props.totalWasted.toHumanReadable()
                ratio.subtitle = "%.2f".format(props.shareRatio)
                timeActive.subtitle = props.timeElapsed.toTime()
                lastComplete.subtitle = props.lastSeen.toDate()
                priority.subtitle = torrent.priority.toString()
                totalSize.subtitle = props.totalSize.toHumanReadable()
                createdBy.subtitle = props.createdBy
                addedOn.subtitle = props.additionDate.toDate()
                completedOn.subtitle = props.completionDate.toDate()
                createdOn.subtitle = props.creationDate.toDate()
                savePath.subtitle = props.savePath
                category.subtitle = torrent.category.ifEmpty { getString(R.string.unspecified) }
                torrentHash.subtitle = torrent.hash
                comment.subtitle = props.comment.ifEmpty { getString(R.string.unspecified) }
            }
        }
    }
}
