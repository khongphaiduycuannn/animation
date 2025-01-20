package com.ndmq.android_mvvm_project_base.custom_view.draw_view.model

import android.graphics.Paint
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson.SerializablePath

sealed class DrawAction {

    data class Brush(val path: SerializablePath, val paint: Paint) : DrawAction()

    data class Erase(val path: SerializablePath) : DrawAction()

    data class Shape(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val shapeType: Int,
        val paint: Paint
    ) : DrawAction()

    data class Fill(val x: Int, val y: Int, val color: Int) : DrawAction()

    data class ImageSticker(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val path: String
    ) : DrawAction()

    data class Text(
        val x: Float,
        val y: Float,
        val textSize: Float,
        val width: Int,
        val scale: Float,
        val angle: Float,
        val color: Int,
        val text: String,
        val isBold: Boolean = false,
        val isNormal: Boolean = false
    ) : DrawAction()
}


object ShapeType {

    const val OVAL = 1

    const val RECTANGLE = 2

    const val TRIANGLE = 3
}