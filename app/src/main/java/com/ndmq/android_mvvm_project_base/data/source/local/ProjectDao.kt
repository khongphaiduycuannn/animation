package com.ndmq.android_mvvm_project_base.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("select * from draw_project")
    fun getAllProjects(): Flow<List<DrawProject>>

    @Insert
    fun createProject(project: DrawProject): Long

    @Query("select * from draw_project where id = :id")
    fun getProjectFlowById(id: Long): Flow<DrawProject>

    @Query("select * from draw_project where id = :id")
    fun getProjectById(id: Long): DrawProject

    @Update
    suspend fun updateProject(project: DrawProject)
}