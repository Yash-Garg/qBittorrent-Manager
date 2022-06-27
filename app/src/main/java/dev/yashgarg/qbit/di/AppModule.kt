package dev.yashgarg.qbit.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.yashgarg.qbit.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideRoomDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.dbName).build()

    @Singleton @Provides fun provideConfigDao(db: AppDatabase) = db.configDao()
}
