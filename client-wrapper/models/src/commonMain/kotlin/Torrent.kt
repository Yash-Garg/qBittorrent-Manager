package qbittorrent.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Torrent(
    /** Time (Unix Epoch) when the torrent was added to the client */
    @SerialName("added_on") val addedOn: Long,
    /** Amount of data left to download (bytes) */
    @SerialName("amount_left") val amountLeft: Long,
    /** Whether this torrent is managed by Automatic Torrent Management */
    @SerialName("auto_tmm") val autoTmm: Boolean,
    /** Percentage of file pieces currently available */
    val availability: Float,
    /** Category of the torrent */
    val category: String,
    /** Amount of transfer data completed (bytes) */
    val completed: Long,
    /** Time (Unix Epoch) when the torrent completed */
    @SerialName("completion_on") val completedOn: Long,
    /**
     * Absolute path of torrent content (root path for multifile torrents, absolute file path for
     * singlefile torrents)
     */
    @SerialName("content_path") val contentPath: String,
    /** Torrent download speed limit (bytes/s). -1 if unlimited. */
    @SerialName("dl_limit") val dlLimit: Long,
    /** Torrent download speed (bytes/s) */
    val dlspeed: Long,
    /** Amount of data downloaded */
    val downloaded: Float,
    /** Amount of data downloaded this session */
    @SerialName("downloaded_session") val downloadedSession: Float,
    /** Torrent ETA (seconds) */
    val eta: Long,
    /** True if first last piece are prioritized */
    @SerialName("f_l_piece_prio") val firstLastPiecePriority: Boolean,
    /** True if force start is enabled for this torrent */
    @SerialName("force_start") val forceStart: Boolean,
    /** Torrent hash */
    val hash: String,
    /** Last time (Unix Epoch) when a chunk was downloaded/uploaded */
    @SerialName("last_activity") val lastActivity: Long,
    /** Magnet URI corresponding to this torrent */
    @SerialName("magnet_uri") val magnetUri: String,
    /** Maximum share ratio until torrent is stopped from seeding/uploading */
    @SerialName("max_ratio") val maxRatio: Float,
    /** Maximum seeding time (seconds) until torrent is stopped from seeding */
    @SerialName("max_seeding_time") val maxSeedingTime: Long,
    /** Torrent name */
    val name: String,
    /** Number of seeds in the swarm */
    @SerialName("num_complete") val seedsInSwarm: Int,
    /** Number of leechers in the swarm */
    @SerialName("num_incomplete") val leechersInSwarm: Int,
    /** Number of leechers connected to */
    @SerialName("num_leechs") val connectedLeechers: Int,
    /** Number of seeds connected to */
    @SerialName("num_seeds") val connectedSeeds: Int,
    /** Torrent priority. Returns -1 if queuing is disabled or torrent is in seed mode */
    val priority: Int,
    /** Torrent progress (percentage/100) */
    val progress: Float,
    /** Torrent share ratio. Max ratio value: 9999. */
    val ratio: Float,
    /**  */
    @SerialName("ratio_limit") val ratioLimit: Float,
    /** Path where this torrent's data is stored */
    @SerialName("save_path") val savePath: String,
    /**  */
    @SerialName("seeding_time_limit") val seedingTimeLimit: Long,
    /** Time (Unix Epoch) when this torrent was last seen complete */
    @SerialName("seen_complete") val seenCompleted: Long,
    /** True if sequential download is enabled */
    @SerialName("seq_dl") val sequentialDownload: Boolean,
    /** Total size (bytes) of files selected for download */
    val size: Long,
    /** Torrent state. */
    val state: State,
    /** True if super seeding is enabled */
    @SerialName("super_seeding") val superSeeding: Boolean,
    /** Tag list of the torrent */
    @Serializable(with = TagListSerializer::class) val tags: List<String>,
    /** Total active time (seconds) */
    @SerialName("time_active") val timeActive: Long,
    /** Torrent elapsed time while complete (seconds) */
    @SerialName("seeding_time") val seedingTime: Long,
    /** Total size (bytes) of all file in this torrent (including unselected ones) */
    @SerialName("total_size") val totalSize: Long,
    /** The first tracker with working status. Returns empty string if no tracker is working. */
    val tracker: String,
    /** Torrent upload speed limit (bytes/s). -1 if unlimited. */
    @SerialName("up_limit") val uploadLimit: Long,
    /** Amount of data uploaded */
    val uploaded: Long,
    /** Amount of data uploaded this session */
    @SerialName("uploaded_session") val uploadedSession: Long,
    /** Torrent upload speed (bytes/s) */
    @SerialName("upspeed") val uploadSpeed: Long
) {
    @Serializable
    enum class State {
        @SerialName("error") ERROR,
        @SerialName("missingFiles") MISSING_FILES,
        @SerialName("uploading") UPLOADING,
        @SerialName("pausedUP") PAUSED_UP,
        @SerialName("queuedUP") QUEUED_UP,
        @SerialName("stalledUP") STALLED_UP,
        @SerialName("checkingUP") CHECKING_UP,
        @SerialName("forcedUP") FORCED_UP,
        @SerialName("allocating") ALLOCATING,
        @SerialName("downloading") DOWNLOADING,
        @SerialName("metaDL") META_DL,
        @SerialName("pausedDL") PAUSED_DL,
        @SerialName("stalledDL") STALLED_DL,
        @SerialName("checkingDL") CHECKING_DL,
        @SerialName("forcedDL") FORCED_DL,
        @SerialName("queuedDL") QUEUED_DL,
        @SerialName("checkingResumeData") CHECKING_RESUME_DATA,
        @SerialName("moving") MOVING,
        @SerialName("unknown") UNKNOWN
    }
}
