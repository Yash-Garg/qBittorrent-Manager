package dev.yashgarg.qbit.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.yashgarg.qbit.data.AppDatabase
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.data.manager.ClientManager
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideRoomDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.dbName).build()

    @Singleton @Provides fun provideConfigDao(db: AppDatabase) = db.configDao()

    @ApplicationScope @Provides fun provideCoroutineScope() = MainScope()

    @Singleton
    @Provides
    fun provideClientManager(configDao: ConfigDao, @ApplicationScope scope: CoroutineScope) =
        ClientManager(configDao, scope)
}

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class ApplicationScope
