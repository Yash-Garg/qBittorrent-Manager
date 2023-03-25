package dev.yashgarg.qbit.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.yashgarg.qbit.data.AppDatabase
import dev.yashgarg.qbit.data.MIGRATION_1_2
import dev.yashgarg.qbit.data.QbitRepository
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.data.manager.ClientManagerImpl
import dev.yashgarg.qbit.data.models.ServerPreferences
import dev.yashgarg.qbit.data.preferences.ServerPreferencesSerializer
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideRoomDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()

    @Singleton @Provides fun provideConfigDao(db: AppDatabase) = db.configDao()

    @ApplicationScope @Provides fun provideCoroutineScope() = MainScope()

    @Provides
    fun provideClientManager(clientManager: ClientManagerImpl): ClientManager = clientManager

    @Provides
    fun provideQbitRepository(clientManager: ClientManager) =
        QbitRepository(Dispatchers.IO, clientManager)

    @Singleton
    @Provides
    fun provideServerPreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<ServerPreferences> {
        return DataStoreFactory.create(
            serializer = ServerPreferencesSerializer,
            corruptionHandler =
                ReplaceFileCorruptionHandler(produceNewData = { ServerPreferences() }),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = {
                appContext.preferencesDataStoreFile(ServerPreferencesSerializer.SERVER_PREFS_NAME)
            }
        )
    }
}

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ApplicationScope
