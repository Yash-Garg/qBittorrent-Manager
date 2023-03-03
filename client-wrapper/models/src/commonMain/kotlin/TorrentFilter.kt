package qbittorrent.models

enum class TorrentFilter {
    ALL,
    DOWNLOADING,
    COMPLETED,
    PAUSED,
    ACTIVE,
    INACTIVE,
    RESUMED,
    STALLED,
    STALLED_UPLOADING,
    STALLED_DOWNLOADING
}
