package dev.yashgarg.qbit.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.yashgarg.qbit.models.ServerConfig

@Database(entities = [ServerConfig::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao

    companion object {
        const val dbName = "qbit-db"
    }
}
