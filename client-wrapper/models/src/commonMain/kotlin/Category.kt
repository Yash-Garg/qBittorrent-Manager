package qbittorrent.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val name: String,
    val savePath: String,
)
