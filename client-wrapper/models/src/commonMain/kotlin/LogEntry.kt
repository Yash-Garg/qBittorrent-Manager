package qbittorrent.models

import kotlinx.serialization.Serializable

@Serializable
data class LogEntry(
    /** ID of the message */
    val id: Int,
    /** Text of the message */
    val message: String,
    /** Milliseconds since epoch */
    val timestamp: Long,
    /**
     * Type of the message
     *
     * @see TYPE_NORMAL
     * @see TYPE_INFO
     * @see TYPE_WARNING
     * @see TYPE_CRITICAL
     */
    val type: Int
) {
    companion object {
        const val TYPE_NORMAL = 1
        const val TYPE_INFO = 2
        const val TYPE_WARNING = 4
        const val TYPE_CRITICAL = 8
    }
}
