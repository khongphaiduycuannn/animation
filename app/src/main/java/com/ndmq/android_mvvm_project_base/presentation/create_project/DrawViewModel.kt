package com.ndmq.android_mvvm_project_base.presentation.create_project

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ndmq.android_mvvm_project_base.base.BaseViewModel
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.DrawView
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.DrawView.Companion.DEFAULT_STROKE_WIDTH
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson.GsonConfig
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.ShapeType
import com.ndmq.android_mvvm_project_base.data.model.DrawProject
import com.ndmq.android_mvvm_project_base.data.model.Frame
import com.ndmq.android_mvvm_project_base.data.model.Sticker
import com.ndmq.android_mvvm_project_base.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val repository: ProjectRepository
) : BaseViewModel() {

    var projectFolderName = ""


    var currentProject: Flow<DrawProject>? = null

    var newestProject: DrawProject? = null

    fun getProject(id: Long) {
        if (currentProject != null) return
        currentProject = repository.getProjectFlow(id)
    }


    val frames = MutableLiveData<MutableList<Frame>>()

    var currentFrameIndex = 0

    fun addNewFrame() {
        viewModelScope.launch {
            frames.value?.add(Frame(listOf(), listOf()))
            newestProject?.let {
                it.frames = GsonConfig.getConfigurationGson().toJson(frames.value)
                repository.updateProject(it)
            }
        }
    }

    fun updateCurrentFrame(frame: Frame) {
        viewModelScope.launch {
            frames.value?.set(currentFrameIndex, frame)
            newestProject?.let {
                it.frames = GsonConfig.getConfigurationGson().toJson(frames.value)
                repository.updateProject(it)
            }
        }
    }


    val currentMode = MutableLiveData(DrawView.Mode.BRUSH)


    val brushColor = MutableLiveData(Color.BLACK)

    val brushWidth = MutableLiveData(DEFAULT_STROKE_WIDTH)


    val eraseWidth = MutableLiveData(DEFAULT_STROKE_WIDTH)


    val fillColor = MutableLiveData(Color.BLACK)


    val dropColor = MutableLiveData(Color.BLACK)


    val shapeColor = MutableLiveData(Color.BLACK)

    val shapeWidth = MutableLiveData(DEFAULT_STROKE_WIDTH)

    val currentShape = MutableLiveData(ShapeType.RECTANGLE)


    val currentSticker = MutableLiveData<Sticker>()


    val textColor = MutableLiveData(Color.BLACK)
}