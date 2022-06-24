package dev.yashgarg.qbit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.yashgarg.qbit.models.ServerConfig

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs") fun getConfigs(): List<ServerConfig>

    @Insert suspend fun addConfig(config: ServerConfig)
}
