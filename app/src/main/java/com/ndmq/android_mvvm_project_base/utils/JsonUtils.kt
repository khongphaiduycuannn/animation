package com.ndmq.android_mvvm_project_base.utils

import android.content.Context
import java.io.IOException

fun loadJsonFromAsset(context: Context, filePath: String): String? {
    return try {
        val inputStream = context.assets.open(filePath)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (ex: IOException) {
        ex.printStackTrace()
        null
    }
}