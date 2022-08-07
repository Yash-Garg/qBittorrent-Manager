package dev.yashgarg.qbit.data.models

enum class FilePriority {
    NOT_DOWNLOAD,
    NORMAL,
    HIGH,
    MAXIMAL;

    companion object {
        fun valueOf(priority: Int): FilePriority {
            return when (priority) {
                0 -> NOT_DOWNLOAD
                1 -> NORMAL
                6 -> HIGH
                7 -> MAXIMAL
                else -> throw IllegalArgumentException("Invalid priority value received")
            }
        }
    }
}
