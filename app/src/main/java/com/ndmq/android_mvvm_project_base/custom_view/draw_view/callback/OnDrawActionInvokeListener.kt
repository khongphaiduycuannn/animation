package com.ndmq.android_mvvm_project_base.custom_view.draw_view.callback

import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.DrawAction


interface OnDrawActionInvokeListener {

    fun onDrawActionInvoke(action: DrawAction? = null) {}

    fun onStrokeActionInvoke(action: DrawAction.Brush) {}

    fun onEraseActionInvoke(action: DrawAction.Erase) {}

    fun onFillActionInvoke(action: DrawAction.Fill) {}

    fun onDropColorActionInvoke(color: Int) {}

    fun onShapeActionInvoke(action: DrawAction.Shape) {}

    fun onStickerActionInvoke(action: DrawAction.ImageSticker) {}

    fun onTextActionInvoke(action: DrawAction.Text) {}

    fun onUndoActionInvoke() {}

    fun onRedoActionInvoke() {}
}