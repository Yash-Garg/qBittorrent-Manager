package dev.yashgarg.qbit.ui.server

import qbittorrent.models.MainData

data class ServerState(
    val dataLoading: Boolean = true,
    val data: MainData? = null,
    val serverName: String? = null,
    val hasError: Boolean = false,
    val error: Throwable? = null
)
