package com.ndmq.android_mvvm_project_base.di

import com.ndmq.android_mvvm_project_base.data.repository.ProjectRepository
import com.ndmq.android_mvvm_project_base.data.source.local.ProjectDao
import com.ndmq.android_mvvm_project_base.utils.FileUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideProjectRepository(local: ProjectDao, fileUtils: FileUtils): ProjectRepository {
        return ProjectRepository(local, fileUtils)
    }
}