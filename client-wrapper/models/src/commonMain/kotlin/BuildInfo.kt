package qbittorrent.models

import kotlinx.serialization.Serializable

@Serializable
data class BuildInfo(
    val qt: String,
    val libtorrent: String,
    val boost: String,
    val openssl: String,
    val bitness: Int,
)
