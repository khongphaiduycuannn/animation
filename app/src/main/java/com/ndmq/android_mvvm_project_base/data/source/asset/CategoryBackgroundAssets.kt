package com.ndmq.android_mvvm_project_base.data.source.asset

import com.google.gson.annotations.SerializedName
import com.ndmq.android_mvvm_project_base.data.model.Background
import com.ndmq.android_mvvm_project_base.data.model.CategoryBackgrounds

data class CategoryBackgroundsRoot(
    @SerializedName("categories_background")
    val categoryBackgroundAssets: List<CategoryBackgroundAssets>
)


data class CategoryBackgroundAssets(
    @SerializedName("category_folder")
    val categoryFolder: String,

    @SerializedName("category_name")
    val categoryName: String,

    @SerializedName("size")
    val size: Int = 0,

    @SerializedName("status")
    val status: String,

    @SerializedName("reward_background_indexes")
    val rewardBackgroundIndexes: List<Int>,

    var backgrounds: List<Background>
)

fun CategoryBackgroundAssets.toCategoryBackgrounds(): CategoryBackgrounds {
    return CategoryBackgrounds(
        categoryName = categoryName,
        size = size,
        status = status,
        backgrounds = backgrounds
    )
}

fun List<CategoryBackgroundAssets>.toCategoryBackgroundList(): List<CategoryBackgrounds> {
    return map { it.toCategoryBackgrounds() }
}