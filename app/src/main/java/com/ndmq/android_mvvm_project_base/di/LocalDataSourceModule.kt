package com.ndmq.android_mvvm_project_base.di

import android.content.Context
import com.ndmq.android_mvvm_project_base.data.source.local.AppRoomDatabase
import com.ndmq.android_mvvm_project_base.data.source.local.ProjectDao
import com.ndmq.android_mvvm_project_base.utils.FileUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalDataSourceModule {

    @Provides
    @Singleton
    fun provideDao(
        @ApplicationContext context: Context
    ): ProjectDao {
        return AppRoomDatabase.getDatabase(context).projectDao
    }

    @Provides
    @Singleton
    fun provideFileUtils(
        @ApplicationContext context: Context
    ): FileUtils {
        return FileUtils(context)
    }
}