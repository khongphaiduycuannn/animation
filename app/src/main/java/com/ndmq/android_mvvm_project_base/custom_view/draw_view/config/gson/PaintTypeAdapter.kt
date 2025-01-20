package com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson

import android.graphics.Paint
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class PaintTypeAdapter : JsonSerializer<Paint>, JsonDeserializer<Paint> {

    override fun serialize(
        src: Paint, typeOfSrc: Type?, context: JsonSerializationContext?
    ): JsonElement {
        val json = JsonObject()
        json.addProperty("color", src.color)
        json.addProperty("strokeWidth", src.strokeWidth)
        json.addProperty("style", src.style.name)
        json.addProperty("strokeCap", src.strokeCap.name)
        json.addProperty("strokeJoin", src.strokeJoin.name)
        return json
    }

    override fun deserialize(
        json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?
    ): Paint {
        val obj = json.asJsonObject
        val paint = Paint()
        paint.color = obj.get("color").asInt
        paint.strokeWidth = obj.get("strokeWidth").asFloat
        paint.style = Paint.Style.valueOf(obj.get("style").asString)
        paint.strokeCap = Paint.Cap.valueOf(obj.get("strokeCap").asString)
        paint.strokeJoin = Paint.Join.valueOf(obj.get("strokeJoin").asString)
        return paint
    }
}