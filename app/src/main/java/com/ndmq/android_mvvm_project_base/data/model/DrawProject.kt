package com.ndmq.android_mvvm_project_base.data.model

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "draw_project")
data class DrawProject(
    val projectName: String,
    val outputFormat: String,
    val widthHeightRatio: Float,
    val fps: Int,
    val backgroundColor: Int?,
    val backgroundPath: String?,
    val audioPath: String?,
    var frames: String, /* Convert Frame to Json string */
    val createdTime: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    companion object ProjectConstants {
        const val MP4 = "MP4"
        const val GIF = "GIF"
    }
}


val defaultDrawProject = DrawProject(
    projectName = "DEMO_PROJECT",
    outputFormat = DrawProject.MP4,
    widthHeightRatio = 1f,
    fps = 7,
    backgroundColor = Color.WHITE,
    backgroundPath = null,
    audioPath = null,
    frames = Gson().toJson(listOf(Frame(listOf(), listOf()))),
    createdTime = 0
)