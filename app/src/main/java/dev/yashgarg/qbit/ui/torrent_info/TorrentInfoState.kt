package dev.yashgarg.qbit.ui.torrent_info

import qbittorrent.models.Torrent

data class TorrentInfoState(
    val loading: Boolean = true,
    val torrent: Torrent? = null,
)
