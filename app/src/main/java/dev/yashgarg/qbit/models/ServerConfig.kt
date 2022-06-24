package dev.yashgarg.qbit.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "configs")
data class ServerConfig(
    @PrimaryKey val configId: Int,
    val serverName: String,
    val baseUrl: String,
    val port: Int = 22,
    val path: String = "/",
    val username: String,
    val password: String,
    val connectionType: ConnectionType = ConnectionType.HTTP,
)
