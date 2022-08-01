package dev.yashgarg.qbit.ui.torrent

import qbittorrent.models.Torrent
import qbittorrent.models.TorrentFile
import qbittorrent.models.TorrentProperties
import qbittorrent.models.TorrentTracker

data class TorrentDetailsState(
    val loading: Boolean = true,
    val torrent: Torrent? = null,
    val torrentFiles: List<TorrentFile> = emptyList(),
    val trackers: List<TorrentTracker> = emptyList(),
    val torrentProperties: TorrentProperties? = null,
    val error: Exception? = null
)
