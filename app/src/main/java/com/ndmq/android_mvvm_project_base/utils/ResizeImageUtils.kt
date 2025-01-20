package com.ndmq.android_mvvm_project_base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import java.io.InputStream

class ResizeImageUtils(
    val context: Context,
    var maxWidth: Int = DEFAULT_MAX_WIDTH,
    var maxHeight: Int = DEFAULT_MAX_HEIGHT
) {

    companion object {

        const val DEFAULT_MAX_WIDTH = 1080

        const val DEFAULT_MAX_HEIGHT = 1920
    }


    private var imageOrientation: Float = 0.0f

    private var imageWidth: Int = 0

    private var imageHeight: Int = 0


    fun getResizedImageBitmap(imageUri: Uri): Bitmap? {
        getDesiredWidthHeight(imageUri).let {
            imageWidth = it.first
            imageHeight = it.second
        }
        imageOrientation = getImageOrientation(imageUri)

        return getResizedOriginalImage(imageUri)
    }


    private fun getDesiredWidthHeight(imageUri: Uri): Pair<Int, Int> {
        val options = Options().apply {
            inJustDecodeBounds = true
        }

        val inputStream = context.contentResolver.openInputStream(imageUri)
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        val width = options.outWidth
        val height = options.outHeight
        val widthHeightRatio = 1f * width / height

        var desiredWidth: Float = 1f * maxWidth
        var desiredHeight: Float = 1f * desiredWidth / widthHeightRatio

        if (desiredHeight > maxHeight) {
            desiredHeight = maxHeight.toFloat()
            desiredWidth = desiredHeight * widthHeightRatio
        }

        return (desiredWidth.toInt() to desiredHeight.toInt())
    }

    private fun getImageOrientation(imageUri: Uri): Float {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(imageUri)

            val orientation = inputStream?.let {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90.0f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180.0f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270.0f
                else -> 0.0f
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return 0.0f
        } finally {
            inputStream?.close()
        }
    }

    private fun getResizedOriginalImage(imageUri: Uri): Bitmap? {
        val options = Options().apply {
            inJustDecodeBounds = false
        }

        val inputStream = context.contentResolver.openInputStream(imageUri)

        val bitmap = try {
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
        }

        if (bitmap == null || bitmap.isRecycled) {
            return null
        }

        val widthScale = 1f * imageWidth / options.outWidth
        val heightScale = 1f * imageHeight / options.outHeight
        val transformMatrix = Matrix().apply {
            postScale(widthScale, heightScale)
            postRotate(imageOrientation)
        }
        val resultBitmap = Bitmap
            .createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, transformMatrix, true)
        bitmap.recycle()

        return resultBitmap
    }
}