package com.ndmq.android_mvvm_project_base.custom_view.draw_view

import android.content.Context
import android.util.AttributeSet
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.constants.ViewConstants.DEFAULT_HEIGHT_WIDTH_RATIO
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.constants.ViewConstants.DEFAULT_WIDTH_PIXEL
import com.ndmq.android_mvvm_project_base.custom_view.sticker_view.StickerView

class StickerLayerView : StickerView {

    /* Attrs */
    var heightWidthRatio = DEFAULT_HEIGHT_WIDTH_RATIO
        set(value) {
            field = value
            requestLayout()
        }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = DEFAULT_WIDTH_PIXEL
        val height = (width * heightWidthRatio).toInt()

        setMeasuredDimension(width, height)
    }


    /* Constructors */
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}