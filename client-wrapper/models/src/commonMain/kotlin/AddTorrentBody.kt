package qbittorrent.models

import kotlin.time.*

data class AddTorrentBody(
    /** Torrent file HTTP or Magnet urls. */
    val urls: MutableList<String> = mutableListOf(),
    /** File paths to torrent files. */
    val torrents: MutableList<String> = mutableListOf(),
    /** Torrent file names and their file contents. */
    val rawTorrents: MutableMap<String, ByteArray> = mutableMapOf(),
    /** The torrent download folder. */
    var savepath: String? = null,
    /** Cookie sent to download the .torrent file. */
    var cookie: String? = null,
    /** Category for the torrent. */
    var category: String? = null,
    /** Tags for the torrent. */
    val tags: MutableList<String> = mutableListOf(),
    /** Skip hash checking. Possible values are true, false (default) */
    var skipChecking: Boolean? = null,
    /** Add torrents in the paused state. Possible values are true, false (default) */
    var paused: Boolean? = null,
    /** Create the root folder. Possible values are true, false, unset (default) */
    var rootFolder: Boolean? = null,
    /** Rename torrent. */
    var rename: String? = null,
    /** Set torrent upload speed limit. Unit in bytes/second */
    var upLimit: Long? = null,
    /** Set torrent download speed limit. Unit in bytes/second */
    var dlLimit: Long? = null,
    /** Set torrent share ratio limit. */
    var ratioLimit: Float? = null,
    /** Set torrent seeding time limit. */
    var seedingTimeLimit: Duration? = null,
    /** Whether Automatic Torrent Management should be used. */
    var autoTMM: Boolean? = null,
    /** Enable sequential download. Possible values are true, false (default) */
    var sequentialDownload: Boolean? = null,
    /** Prioritize download first last piece. Possible values are true, false (default) */
    var firstLastPiecePriority: Boolean? = null,
)
