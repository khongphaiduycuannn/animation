package com.ndmq.android_mvvm_project_base.presentation.create_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ndmq.android_mvvm_project_base.base.BaseViewModel
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import com.ndmq.android_mvvm_project_base.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val repository: ProjectRepository,
) : BaseViewModel() {

    val project = MutableLiveData<DrawProject>()

    fun getProject(id: Long) {
        viewModelScope.launch {
            project.value = repository.getProject(id)
        }
    }
}