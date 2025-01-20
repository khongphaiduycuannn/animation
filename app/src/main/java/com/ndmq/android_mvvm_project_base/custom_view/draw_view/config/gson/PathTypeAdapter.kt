package com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type


class PathTypeAdapter : JsonSerializer<SerializablePath?>, JsonDeserializer<SerializablePath?> {

    override fun serialize(
        serializablePath: SerializablePath?, typeOfSrc: Type?, context: JsonSerializationContext?
    ): JsonElement {
        val jsonObject = JsonObject()
        val commandsArray = JsonArray()

        serializablePath?.let { path ->
            for (command in path.getCommands()) {
                val commandObject = JsonObject()
                commandObject.addProperty("type", command.type)

                val paramsArray = JsonArray()
                for (param in command.params) {
                    paramsArray.add(param)
                }
                commandObject.add("params", paramsArray)

                commandsArray.add(commandObject)
            }

            jsonObject.add("paths", commandsArray)
        }

        return jsonObject
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SerializablePath {
        val path = SerializablePath()
        val commandsArray = json.asJsonObject.getAsJsonArray("paths")

        for (commandElement in commandsArray) {
            val commandObject = commandElement.asJsonObject
            val type = commandObject["type"].asString
            val paramsArray = commandObject.getAsJsonArray("params")

            val params = FloatArray(paramsArray.size())
            for (i in 0 until paramsArray.size()) {
                params[i] = paramsArray[i].asFloat
            }

            when (type) {
                "moveTo" -> path.moveTo(params[0], params[1])
                "lineTo" -> path.lineTo(params[0], params[1])
                "quadTo" -> path.quadTo(params[0], params[1], params[2], params[3])
                "close" -> path.close()
            }
        }

        return path
    }
}