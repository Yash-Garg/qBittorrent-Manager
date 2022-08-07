package dev.yashgarg.qbit.ui.torrent

import dev.yashgarg.qbit.data.models.ContentTreeItem
import qbittorrent.models.*

data class TorrentDetailsState(
    val loading: Boolean = true,
    val peersLoading: Boolean = true,
    val peers: TorrentPeers? = null,
    val torrent: Torrent? = null,
    val contentTree: List<ContentTreeItem> = emptyList(),
    val contentLoading: Boolean = true,
    val trackers: List<TorrentTracker> = emptyList(),
    val torrentProperties: TorrentProperties? = null,
    val error: Exception? = null
)
