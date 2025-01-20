package com.ndmq.android_mvvm_project_base.data.model

import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.DrawAction

data class Frame(
    val actions: List<DrawAction>,
    val redoActions: List<DrawAction>
) {

    companion object {

        val EMPTY_FRAME = Frame(emptyList(), emptyList())
    }
}