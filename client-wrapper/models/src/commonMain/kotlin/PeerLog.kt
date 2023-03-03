package qbittorrent.models

import kotlinx.serialization.Serializable

@Serializable
data class PeerLog(
    val id: Int,
    val ip: String,
    val timestamp: Long,
    val blocked: Boolean,
    val reason: String,
)
