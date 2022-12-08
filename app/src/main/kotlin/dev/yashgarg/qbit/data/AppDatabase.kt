package dev.yashgarg.qbit.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.data.models.ServerConfig

@Database(
    entities = [ServerConfig::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 2, to = 3)],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao

    companion object {
        const val DB_NAME = "qbit-db"
    }
}

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            with(database) {
                execSQL(
                    "CREATE TABLE IF NOT EXISTS configsTmp (config_id INTEGER NOT NULL, serverName TEXT NOT NULL, " +
                        "baseUrl TEXT NOT NULL, port INTEGER, username TEXT NOT NULL, password TEXT NOT NULL, connectionType TEXT NOT NULL, " +
                        "trustSelfSigned INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(config_id))"
                )

                execSQL(
                    "INSERT INTO configsTmp (config_id, serverName, baseUrl, port, username, password, " +
                        "connectionType) SELECT config_id, serverName, baseUrl, port, username, password, connectionType FROM configs"
                )

                execSQL("UPDATE configsTmp SET port = NULL WHERE port = 443")

                execSQL("DROP TABLE configs")

                execSQL("ALTER TABLE configsTmp RENAME TO configs")
            }
        }
    }
