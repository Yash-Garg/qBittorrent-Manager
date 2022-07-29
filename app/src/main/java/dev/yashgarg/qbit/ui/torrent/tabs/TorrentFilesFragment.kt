package dev.yashgarg.qbit.ui.torrent.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsViewModel

class TorrentFilesFragment : Fragment() {
    private val viewModel by
        viewModels<TorrentDetailsViewModel>(ownerProducer = { requireParentFragment() })
}
