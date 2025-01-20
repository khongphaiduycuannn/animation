package com.ndmq.android_mvvm_project_base.data.source.asset

import android.content.Context
import com.google.gson.Gson
import com.ndmq.android_mvvm_project_base.data.model.Background
import com.ndmq.android_mvvm_project_base.data.model.CategoryBackgrounds
import com.ndmq.android_mvvm_project_base.utils.constant.AssetPathConstants.ASSETS_PATH
import com.ndmq.android_mvvm_project_base.utils.constant.AssetPathConstants.BACKGROUND_FOLDER
import com.ndmq.android_mvvm_project_base.utils.constant.AssetPathConstants.BACKGROUND_JSON_PATH
import com.ndmq.android_mvvm_project_base.utils.loadJsonFromAsset

object BackgroundUtils {

    fun getCategoryBackgroundList(context: Context): List<CategoryBackgrounds> {
        val json = loadJsonFromAsset(context, BACKGROUND_JSON_PATH)
        val gson = Gson()
        val root = gson.fromJson(json, CategoryBackgroundsRoot::class.java) ?: return listOf()

        val categories = root.categoryBackgroundAssets
        for (i in categories.indices) {
            val category = categories[i]
            val backgrounds = mutableListOf<Background>()
            val rewardBackgroundIndexes = category.rewardBackgroundIndexes.toSet()

            for (j in 1..category.size) {
                val path = "$ASSETS_PATH/$BACKGROUND_FOLDER/${category.categoryFolder}/$j.webp"
                val status =
                    if (rewardBackgroundIndexes.contains(j) || category.status == "reward")
                        "reward"
                    else "free"

                val background = Background(path, status)
                backgrounds.add(background)
            }

            category.backgrounds = backgrounds
        }
        return categories.toCategoryBackgroundList()
    }
}