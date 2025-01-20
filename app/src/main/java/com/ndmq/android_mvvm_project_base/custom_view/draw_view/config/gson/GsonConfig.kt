package com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson

import android.graphics.Paint
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.DrawAction

object GsonConfig {

    private val drawActionAdapterFactory = RuntimeTypeAdapterFactory.of(
        DrawAction::class.java,
        "type"
    )
        .registerSubtype(DrawAction.Brush::class.java, "Brush")
        .registerSubtype(DrawAction.Erase::class.java, "Erase")
        .registerSubtype(DrawAction.Shape::class.java, "Shape")
        .registerSubtype(DrawAction.Fill::class.java, "Fill")
        .registerSubtype(DrawAction.Text::class.java, "Text")
        .registerSubtype(DrawAction.ImageSticker::class.java, "Image Sticker")

    fun getConfigurationGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapterFactory(drawActionAdapterFactory)
            .registerTypeAdapter(SerializablePath::class.java, PathTypeAdapter())
            .registerTypeAdapter(Paint::class.java, PaintTypeAdapter())
            .create()
    }
}