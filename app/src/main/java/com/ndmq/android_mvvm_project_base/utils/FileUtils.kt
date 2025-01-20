package com.ndmq.android_mvvm_project_base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

const val TEMP_IMAGE_FILE_NAME = "temp_image.jpg"

class FileUtils(
    val context: Context
) {

    fun createFolder(folderName: String): Boolean {
        val path = "${context.filesDir.absolutePath}/$folderName"
        val folder = File(path)

        return if (!folder.exists()) {
            folder.mkdirs()
            true
        } else {
            false
        }
    }

    fun createFile(fileName: String): File {
        val path = "${context.filesDir.absolutePath}/$fileName"
        val file = File(path)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    fun createNewPngFile(folderPath: String): File {
        var path = "${context.filesDir.absolutePath}/$folderPath/${getRandomName()}"
        var file = File(path)
        while (file.exists()) {
            path = "${context.filesDir.absolutePath}/$folderPath/${getRandomName()}"
            file = File(path)
        }
        file.createNewFile()
        return file
    }


    fun getBitmapFromAssetPath(assetPath: String): Bitmap? {
        return try {
            val actualAssetPath = assetPath.replace("file:///android_asset/", "")
            val inputStream = context.assets.open(actualAssetPath)
            val temp = BitmapFactory.decodeStream(inputStream)
            temp
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun getBitmapFromFilePath(filePath: String): Bitmap? {
        return try {
            val inputStream = FileInputStream(filePath)
            val temp = BitmapFactory.decodeStream(inputStream)
            temp
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun saveBitmapToPng(folderPath: String, bitmap: Bitmap): String? =
        withContext(Dispatchers.IO) {
            try {
                createFolder(folderPath)

                val file = createNewPngFile(folderPath)
                val outputStream = FileOutputStream(file)

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
                return@withContext file.path
            } catch (ex: Exception) {
                ex.printStackTrace()
                return@withContext null
            }
        }


    private fun getRandomName(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("") + ".png"
    }
}