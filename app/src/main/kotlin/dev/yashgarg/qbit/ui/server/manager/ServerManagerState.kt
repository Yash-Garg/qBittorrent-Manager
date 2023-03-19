package dev.yashgarg.qbit.ui.server.manager

import dev.yashgarg.qbit.data.models.ServerConfig

data class ServerManagerState(
    val configsLoading: Boolean = true,
    val configs: List<ServerConfig> = emptyList(),
    val error: Boolean = false
)
