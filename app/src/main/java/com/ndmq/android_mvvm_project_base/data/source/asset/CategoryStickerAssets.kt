package com.ndmq.android_mvvm_project_base.data.source.asset

import com.google.gson.annotations.SerializedName
import com.ndmq.android_mvvm_project_base.data.model.CategoryStickers
import com.ndmq.android_mvvm_project_base.data.model.Sticker


data class CategoryStickerRoot(
    @SerializedName("categories_sticker")
    val categoryStickerAssets: List<CategoryStickerAssets>
)


data class CategoryStickerAssets(
    @SerializedName("category_folder")
    val categoryFolder: String,

    @SerializedName("category_name")
    val categoryName: String,

    @SerializedName("size")
    val size: Int = 0,

    @SerializedName("status")
    val status: String,

    @SerializedName("reward_sticker_indexes")
    val rewardStickerIndexes: List<Int>,

    var stickers: List<Sticker>
)

fun CategoryStickerAssets.toCategorySticker(): CategoryStickers {
    return CategoryStickers(
        categoryName = categoryName,
        size = size,
        status = status,
        stickers = stickers
    )
}

fun List<CategoryStickerAssets>.toCategoryStickerList(): List<CategoryStickers> {
    return map { it.toCategorySticker() }
}