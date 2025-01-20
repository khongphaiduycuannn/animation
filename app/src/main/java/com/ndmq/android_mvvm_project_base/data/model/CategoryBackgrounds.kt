package com.ndmq.android_mvvm_project_base.data.model

data class CategoryBackgrounds (
    val categoryName: String,

    val size: Int = 0,

    val status: String,

    var backgrounds: List<Background>
)