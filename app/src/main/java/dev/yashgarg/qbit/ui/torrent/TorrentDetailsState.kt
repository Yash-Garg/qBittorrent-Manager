package dev.yashgarg.qbit.ui.torrent

import qbittorrent.models.*

data class TorrentDetailsState(
    val loading: Boolean = true,
    val peersLoading: Boolean = true,
    val peers: TorrentPeers? = null,
    val torrent: Torrent? = null,
    val torrentFiles: List<TorrentFile> = emptyList(),
    val trackers: List<TorrentTracker> = emptyList(),
    val torrentProperties: TorrentProperties? = null,
    val error: Exception? = null
)
