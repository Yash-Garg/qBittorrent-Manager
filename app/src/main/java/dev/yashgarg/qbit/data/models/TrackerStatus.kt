package dev.yashgarg.qbit.data.models

enum class TrackerStatus {
    DISABLED,
    UPDATING,
    NOT_CONTACTED,
    CONTACTED_WORKING,
    CONTACTED_NOT_WORKING;

    companion object {
        fun statusOf(status: Int): TrackerStatus {
            return when (status) {
                0 -> DISABLED
                1 -> NOT_CONTACTED
                2 -> CONTACTED_WORKING
                3 -> UPDATING
                4 -> CONTACTED_NOT_WORKING
                else -> throw IllegalArgumentException("Invalid status code")
            }
        }
    }
}
