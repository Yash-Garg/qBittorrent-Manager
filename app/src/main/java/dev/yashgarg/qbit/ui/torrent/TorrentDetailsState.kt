package dev.yashgarg.qbit.ui.torrent

import qbittorrent.models.Torrent
import qbittorrent.models.TorrentFile

data class TorrentDetailsState(
    val loading: Boolean = true,
    val torrent: Torrent? = null,
    val torrentFiles: List<TorrentFile> = emptyList(),
)
