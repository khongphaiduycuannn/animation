package com.ndmq.android_mvvm_project_base.presentation.home

import com.ndmq.android_mvvm_project_base.base.BaseViewModel
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import com.ndmq.android_mvvm_project_base.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProjectRepository
) : BaseViewModel() {

    val allProjects: Flow<List<DrawProject>> = repository.allProjects
}