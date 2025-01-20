package com.ndmq.android_mvvm_project_base.data.source.asset

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.ndmq.android_mvvm_project_base.data.model.CategoryStickers
import com.ndmq.android_mvvm_project_base.data.model.Sticker
import com.ndmq.android_mvvm_project_base.utils.FileUtils
import com.ndmq.android_mvvm_project_base.utils.constant.AssetPathConstants.ASSETS_PATH
import com.ndmq.android_mvvm_project_base.utils.constant.AssetPathConstants.STICKER_FOLDER
import com.ndmq.android_mvvm_project_base.utils.constant.AssetPathConstants.STICKER_JSON_PATH
import com.ndmq.android_mvvm_project_base.utils.loadJsonFromAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


object StickerUtils {

    private val stickersBitmap = mutableMapOf<String, Bitmap>()

    fun getCategoryStickerFromAssets(context: Context): List<CategoryStickers> {
        val json = loadJsonFromAsset(context, STICKER_JSON_PATH)
        val gson = Gson()
        val root = gson.fromJson(json, CategoryStickerRoot::class.java) ?: return listOf()

        val categories = root.categoryStickerAssets
        for (i in categories.indices) {
            val category = categories[i]
            val stickers = mutableListOf<Sticker>()
            val rewardStickerIndexes = category.rewardStickerIndexes.toSet()

            for (j in 1..category.size) {
                val path = "$ASSETS_PATH/$STICKER_FOLDER/${category.categoryFolder}/$j.webp"
                val status =
                    if (rewardStickerIndexes.contains(j) || category.status == "reward")
                        "reward"
                    else "free"

                val sticker = Sticker(path, status)
                stickers.add(sticker)
            }

            category.stickers = stickers
        }
        return categories.toCategoryStickerList()
    }

    fun getStickersFromAssets(context: Context): List<Sticker> {
        val categories = getCategoryStickerFromAssets(context)
        val allStickers = mutableListOf<Sticker>()
        for (category in categories) {
            allStickers.addAll(category.stickers)
        }
        return allStickers
    }


    /* Gọi lấy list stickers từ asset để sử dụng sau này*/
    suspend fun loadStickersAssetsBitmap(
        context: Context,
        doOnLoadSuccess: (stickerMap: MutableMap<String, Bitmap>) -> Unit = {}
    ) {
        withContext(Dispatchers.IO) {
            val allStickers = getStickersFromAssets(context)

            val fileUtils = FileUtils(context)
            allStickers.forEach { sticker ->
                fileUtils.getBitmapFromAssetPath(sticker.path)?.let {
                    stickersBitmap[sticker.path] = it
                }
            }

            doOnLoadSuccess(stickersBitmap)
        }
    }

    suspend fun loadProjectStickersBitmap(
        context: Context,
        projectName: String,
        doOnLoadSuccess: (stickerMap: MutableMap<String, Bitmap>) -> Unit = {}
    ) {
        withContext(Dispatchers.IO) {
            try {
                val targetFolder =
                    File("${context.filesDir.absolutePath}/$projectName/sticker_images")
                val files = targetFolder.listFiles() ?: return@withContext

                files.forEach {
                    loadStickerBitmap(context, it.path)
                }

                doOnLoadSuccess(stickersBitmap)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    suspend fun loadStickerBitmap(context: Context, filePath: String) {
        withContext(Dispatchers.IO) {
            val fileUtils = FileUtils(context)
            fileUtils.getBitmapFromFilePath(filePath)?.let {
                stickersBitmap[filePath] = it
            }
        }
    }

    fun getStickerMap() = stickersBitmap
}