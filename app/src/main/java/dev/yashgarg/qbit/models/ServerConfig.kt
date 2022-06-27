package dev.yashgarg.qbit.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "configs")
data class ServerConfig(
    @PrimaryKey @ColumnInfo("config_id") val configId: Int,
    val serverName: String,
    val baseUrl: String,
    val port: Int = 22,
    val username: String,
    val password: String,
    val connectionType: ConnectionType = ConnectionType.HTTP,
)
