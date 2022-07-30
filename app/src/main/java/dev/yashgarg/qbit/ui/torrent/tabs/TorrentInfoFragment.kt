package dev.yashgarg.qbit.ui.torrent.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsViewModel

class TorrentInfoFragment : Fragment(R.layout.torrent_info_fragment) {
    private val viewModel by
        viewModels<TorrentDetailsViewModel>(ownerProducer = { requireParentFragment() })
}
