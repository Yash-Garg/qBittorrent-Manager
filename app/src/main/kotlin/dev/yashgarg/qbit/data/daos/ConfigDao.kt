package dev.yashgarg.qbit.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.yashgarg.qbit.data.models.ServerConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs") fun getConfigs(): Flow<List<ServerConfig>>

    @Query("SELECT * FROM configs WHERE config_id = :index")
    suspend fun getConfigAtIndex(index: Int = 0): ServerConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addConfig(config: ServerConfig)
}
