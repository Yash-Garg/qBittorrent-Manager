package qbittorrent.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TorrentFile(
    /** The index of this file in the torrent contents list */
    val index: Int,
    /** File name (including relative path) */
    val name: String,
    /** File size (bytes) */
    val size: Long,
    /** File progress (percentage/100) */
    val progress: Float,
    /** File priority */
    val priority: Int,
    /** True if file is seeding/complete */
    @SerialName("is_seed") val isSeeding: Boolean? = false,
    /**
     * The first number is the starting piece index and the second number is the ending piece index
     * (inclusive)
     */
    @SerialName("piece_range") val pieceRange: List<Int>,
    /** Percentage of file pieces currently available */
    val availability: Float
)
