package dev.yashgarg.qbit.data.models

import androidx.annotation.Keep
import qbittorrent.models.TorrentFile

@Keep
data class ContentTreeItem(
    val id: Int,
    val name: String,
    val item: TorrentFile? = null,
    val children: List<ContentTreeItem>? = null,
    val size: Long,
    val progress: Long,
)
