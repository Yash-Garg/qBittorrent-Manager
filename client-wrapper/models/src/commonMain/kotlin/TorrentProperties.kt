package qbittorrent.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TorrentProperties(
    /** Torrent save path */
    @SerialName("save_path") val savePath: String,
    /** Torrent creation date (Unix timestamp) */
    @SerialName("creation_date") val creationDate: Long,
    /** Torrent piece size (bytes) */
    @SerialName("piece_size") val pieceSize: Long,
    /** Torrent comment */
    val comment: String,
    /** Total data wasted for torrent (bytes) */
    @SerialName("total_wasted") val totalWasted: Long,
    /** Total data uploaded for torrent (bytes) */
    @SerialName("total_uploaded") val totalUploaded: Long,
    /** Total data uploaded this session (bytes) */
    @SerialName("total_uploaded_session") val totalUploadedSession: Long,
    /** Total data downloaded for torrent (bytes) */
    @SerialName("total_downloaded") val totalDownloaded: Long,
    /** Total data downloaded this session (bytes) */
    @SerialName("total_downloaded_session") val totalDownloadedSession: Long,
    /** Torrent upload limit (bytes/s) */
    @SerialName("up_limit") val upLimit: Long,
    /** Torrent download limit (bytes/s) */
    @SerialName("dl_limit") val dlLimit: Long,
    /** Torrent elapsed time (seconds) */
    @SerialName("time_elapsed") val timeElapsed: Long,
    /** Torrent elapsed time while complete (seconds) */
    @SerialName("seeding_time") val seedingTime: Long,
    /** Torrent connection count */
    @SerialName("nb_connections") val nbConnections: Int,
    /** Torrent connection count limit */
    @SerialName("nb_connections_limit") val nbConnectionsLimit: Int,
    /** Torrent share ratio */
    @SerialName("share_ratio") val shareRatio: Float,
    /** When this torrent was added (unix timestamp) */
    @SerialName("addition_date") val additionDate: Long,
    /** Torrent completion date (unix timestamp) */
    @SerialName("completion_date") val completionDate: Long,
    /** Torrent creator */
    @SerialName("created_by") val createdBy: String,
    /** Torrent average download speed (bytes/second) */
    @SerialName("dl_speed_avg") val dlSpeedAvg: Long,
    /** Torrent download speed (bytes/second) */
    @SerialName("dl_speed") val dlSpeed: Long,
    /** Torrent ETA (seconds) */
    val eta: Long,
    /** Last seen complete date (unix timestamp) */
    @SerialName("last_seen") val lastSeen: Long,
    /** Number of peers connected to */
    val peers: Int,
    /** Number of peers in the swarm */
    @SerialName("peers_total") val peersTotal: Int,
    /** Number of pieces owned */
    @SerialName("pieces_have") val piecesHave: Int,
    /** Number of pieces of the torrent */
    @SerialName("pieces_num") val piecesNum: Int,
    /** Number of seconds until the next announce */
    val reannounce: Long,
    /** Number of seeds connected to */
    val seeds: Int,
    /** Number of seeds in the swarm */
    @SerialName("seeds_total") val seedsTotal: Int,
    /** Torrent total size (bytes) */
    @SerialName("total_size") val totalSize: Long,
    /** Torrent average upload speed (bytes/second) */
    @SerialName("up_speed_avg") val upSpeedAvg: Long,
    /** Torrent upload speed (bytes/second) */
    @SerialName("up_speed") val upSpeed: Long,
)
