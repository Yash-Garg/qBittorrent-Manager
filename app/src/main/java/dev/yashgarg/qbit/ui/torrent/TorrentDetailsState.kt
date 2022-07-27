package dev.yashgarg.qbit.ui.torrent

import qbittorrent.models.Torrent

data class TorrentDetailsState(
    val loading: Boolean = true,
    val torrent: Torrent? = null,
)
