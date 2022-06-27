package dev.yashgarg.qbit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.yashgarg.qbit.models.ServerConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs") fun getConfigs(): Flow<List<ServerConfig>>

    @Query("SELECT * FROM configs WHERE config_id = :index")
    fun getConfigAtIndex(index: Int = 0): Flow<ServerConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addConfig(config: ServerConfig)
}
