package dev.yashgarg.qbit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.data.models.ServerConfig

@Database(entities = [ServerConfig::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao

    companion object {
        const val dbName = "qbit-db"
    }
}
