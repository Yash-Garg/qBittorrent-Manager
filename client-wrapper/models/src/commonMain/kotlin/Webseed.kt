package qbittorrent.models

import kotlinx.serialization.Serializable

@Serializable
data class Webseed(
    /** URL of the web seed. */
    val url: String,
)
