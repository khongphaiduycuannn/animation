package com.ndmq.android_mvvm_project_base.data.repository

import com.ndmq.android_mvvm_project_base.base.BaseRepository
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import com.ndmq.android_mvvm_project_base.data.source.local.ProjectDao
import com.ndmq.android_mvvm_project_base.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val local: ProjectDao,
    private val fileUtils: FileUtils
) : BaseRepository() {

    val allProjects: Flow<List<DrawProject>> = local.getAllProjects()

    fun getProjectFlow(id: Long): Flow<DrawProject> = local.getProjectFlowById(id)

    suspend fun updateProject(project: DrawProject) = withContext(Dispatchers.IO) {
        local.updateProject(project)
    }

    suspend fun getProject(id: Long) = withContext(Dispatchers.IO) {
        local.getProjectById(id)
    }

    suspend fun createProject(project: DrawProject) = getResult {
        val folderName = project.createdTime.toString()
        if (fileUtils.createFolder(folderName)) {
            fileUtils.createFolder("$folderName/sticker_images")
            fileUtils.createFolder("$folderName/background")
            fileUtils.createFolder("$folderName/audio")
            fileUtils.createFolder("$folderName/frames")

            local.createProject(project)
        } else {
            throw Exception("Can't create project folder")
        }
    }
}