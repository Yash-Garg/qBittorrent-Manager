package qbittorrent.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalTransferInfo(
    /** Global download rate (bytes/s) */
    @SerialName("dl_info_speed") val dlInfoSpeed: Long,
    /** Data downloaded this session (bytes) */
    @SerialName("dl_info_data") val dlInfoData: Long,
    /** Global upload rate (bytes/s) */
    @SerialName("up_info_speed") val upInfoSpeed: Long,
    /** Data uploaded this session (bytes) */
    @SerialName("up_info_data") val upInfoData: Long,
    /** Download rate limit (bytes/s) */
    @SerialName("dl_rate_limit") val dlRateLimit: Long,
    /** Upload rate limit (bytes/s) */
    @SerialName("up_rate_limit") val upRateLimit: Long,
    /** DHT nodes connected to */
    @SerialName("dht_nodes") val dhtNodes: Int,
    /** Connection status */
    @SerialName("connection_status") val connectionStatus: ConnectionStatus,
    /** True if torrent queueing is enabled */
    @SerialName("queueing") val queueing: Boolean = false,
    /** True if alternative speed limits are enabled */
    @SerialName("use_alt_speed_limits") val useAltSpeedLimits: Boolean = false,
    /** Transfer list refresh interval (milliseconds) */
    @SerialName("refresh_interval") val refreshInterval: Long = -1,
)

@Serializable
enum class ConnectionStatus {
    @SerialName("connected") CONNECTED,
    @SerialName("firewalled") FIREWALLED,
    @SerialName("disconnected") DISCONNECTED
}
