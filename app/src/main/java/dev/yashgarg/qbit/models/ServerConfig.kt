package dev.yashgarg.qbit.models

import androidx.annotation.Keep

@Keep
data class ServerConfig(
    val serverName: String,
    val baseUrl: String,
    val port: Int = 22,
    val path: String = "/",
    val username: String,
    val password: String,
    val connectionType: ConnectionType = ConnectionType.HTTP,
)
