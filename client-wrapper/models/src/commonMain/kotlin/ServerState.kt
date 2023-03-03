package qbittorrent.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerState(
    /** all time download (bytes) */
    @SerialName("alltime_dl") val allTimeDownload: Long,

    /** all time upload (bytes) */
    @SerialName("alltime_ul") val allTimeUpload: Long,
    @SerialName("average_time_queue") val averageTimeInQueue: Int,

    /** Connection status */
    @SerialName("connection_status") val connectionStatus: ConnectionStatus,
    @SerialName("dht_nodes") val dhtNodes: Int,

    /** Data downloaded this session (bytes) */
    @SerialName("dl_info_data") val dlInfoData: Long,

    /** Global download rate (bytes/s) */
    @SerialName("dl_info_speed") val dlInfoSpeed: Long,

    /** Download rate limit (bytes/s) */
    @SerialName("dl_rate_limit") val dlRateLimit: Long,
    @SerialName("free_space_on_disk") val freeSpace: Long,
    @SerialName("global_ratio") val globalShareRatio: String,
    @SerialName("queued_io_jobs") val queuedIoJobs: Int,

    /** True if torrent queueing is enabled */
    val queueing: Boolean,
    @SerialName("read_cache_hits") val readCacheHits: String,
    @SerialName("read_cache_overload") val readCacheOverload: String,

    /** Transfer list refresh interval (milliseconds) */
    @SerialName("refresh_interval") val refreshInterval: Int,
    @SerialName("total_buffers_size") val totalBuffersSize: Int,
    @SerialName("total_peer_connections") val totalPeerConnections: Int,
    @SerialName("total_queued_size") val totalQueuedSize: Int,
    @SerialName("total_wasted_session") val sessionWaste: Long,

    /** Data uploaded this session (bytes) */
    @SerialName("up_info_data") val upInfoData: Long,

    /** Global upload rate (bytes/s) */
    @SerialName("up_info_speed") val upInfoSpeed: Long,

    /** Upload rate limit (bytes/s) */
    @SerialName("up_rate_limit") val upRateLimit: Int,

    /** True if alternative speed limits are enabled */
    @SerialName("use_alt_speed_limits") val useAltSpeedLimits: Boolean,
    @SerialName("write_cache_overload") val writeCacheOverload: String,
)
