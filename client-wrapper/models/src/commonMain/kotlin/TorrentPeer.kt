package qbittorrent.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TorrentPeer(
    val client: String = "",
    val connection: String = "",
    val country: String = "",
    @SerialName("country_code") val countryCode: String = "",
    @SerialName("dl_speed") val dlSpeed: Long = 0,
    val downloaded: Long = 0,
    val files: String = "",
    val flags: String = "",
    @SerialName("flags_desc") val flagsDesc: String = "",
    val ip: String = "",
    val port: Int = 0,
    val progress: Float = 0f,
    val relevance: Float = 0f,
    @SerialName("up_speed") val upSpeed: Long = 0,
    val uploaded: Long = 0
)
