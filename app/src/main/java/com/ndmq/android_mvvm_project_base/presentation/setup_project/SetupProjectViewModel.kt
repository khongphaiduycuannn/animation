package com.ndmq.android_mvvm_project_base.presentation.setup_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ndmq.android_mvvm_project_base.base.BaseViewModel
import com.ndmq.android_mvvm_project_base.base.DataState
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import com.ndmq.android_mvvm_project_base.data.model.defaultDrawProject
import com.ndmq.android_mvvm_project_base.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SetupProjectViewModel @Inject constructor(
    private val repository: ProjectRepository
) : BaseViewModel() {

    val projectName = MutableLiveData<String>()

    val backgroundColor = MutableLiveData<Int>(defaultDrawProject.backgroundColor)

    val backgroundPath = MutableLiveData<String>()

    val outputFormat = MutableLiveData<String>(defaultDrawProject.outputFormat)

    val widthHeightRatio = MutableLiveData<Float>(defaultDrawProject.widthHeightRatio)

    val fps = MutableLiveData<Int>(defaultDrawProject.fps)


    fun createProject(onProjectCreated: (id: Long, createdTime: Long) -> Unit) {
        viewModelScope.launch {
            if (!isProjectSettingValid()) {
                return@launch
            }

            val createdTime = Date().time
            val project = DrawProject(
                projectName.value!!,
                outputFormat.value!!,
                widthHeightRatio.value!!,
                fps.value!!,
                backgroundColor.value,
                backgroundPath.value,
                "",
                defaultDrawProject.frames,
                createdTime
            )

            when (val result = repository.createProject(project)) {
                is DataState.Success -> {
                    onProjectCreated(result.data, createdTime)
                    message.value = "Create project successfully"
                }

                is DataState.Error -> {
                    message.value = "Project creation failed"
                }
            }
        }
    }

    private fun isProjectSettingValid(): Boolean {
        if (projectName.value.isNullOrEmpty()) {
            message.value = "Project name is required"
            return false
        }

        if (backgroundColor.value == null && backgroundPath.value == null) {
            message.value = "Background is required"
            return false
        }

        if (outputFormat.value == null) {
            message.value = "Output format is required"
            return false
        }

        if (widthHeightRatio.value == null) {
            message.value = "Width height ratio format is required"
            return false
        }

        if (fps.value == null) {
            message.value = "FPS format is required"
            return false
        }

        return true
    }
}