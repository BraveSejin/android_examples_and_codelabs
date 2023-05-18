package com.example.android.hilt.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.example.android.hilt.data.AppDatabase
import com.example.android.hilt.data.LogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 이거 원래 ApplicationComponent였는데 이름 바뀜
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides // appDatabase가 전이 종속 항목 (transitive dependency) -> 힐트에게 알려줘야 한다.
    fun provideLogDao(database: AppDatabase): LogDao {
        return database.logDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "logging.db"
        ).build()
    }
}