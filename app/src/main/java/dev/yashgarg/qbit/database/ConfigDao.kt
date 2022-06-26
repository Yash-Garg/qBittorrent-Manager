package dev.yashgarg.qbit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.yashgarg.qbit.models.ServerConfig

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs") suspend fun getConfigs(): List<ServerConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addConfig(config: ServerConfig)
}
