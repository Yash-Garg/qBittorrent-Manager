package dev.yashgarg.qbit.ui.server

import qbittorrent.models.MainData

data class ServerState(
    val dataLoading: Boolean = true,
    val data: MainData? = null,
    val speedLimitMode: Int = 0,
    val serverName: String? = null,
    val hasError: Boolean = false,
    val error: Throwable? = null
)
