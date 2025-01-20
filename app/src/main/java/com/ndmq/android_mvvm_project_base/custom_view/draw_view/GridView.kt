package com.ndmq.android_mvvm_project_base.custom_view.draw_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.constants.ViewConstants.DEFAULT_HEIGHT_WIDTH_RATIO
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.constants.ViewConstants.DEFAULT_WIDTH_PIXEL

class GridView : View {

    companion object {

        const val VERTICAL_GRID_AMOUNT = 7


        /* Watermark paint */
        const val WATERMARK_ALPHA = 30


        /* Grid paint */
        const val GRID_ALPHA = 30

        const val DEFAULT_GRID_STROKE_WIDTH = 1.2f

        const val DEFAULT_GRID_STROKE_COLOR = Color.BLACK
    }


    /* Paint */
    private val gridPaint = Paint().apply {
        strokeWidth = DEFAULT_GRID_STROKE_WIDTH
        style = Paint.Style.STROKE
        color = DEFAULT_GRID_STROKE_COLOR
        alpha = GRID_ALPHA
    }

    private val watermarkPaint = Paint().apply {
        alpha = WATERMARK_ALPHA
    }


    /* Attrs */
    var heightWidthRatio = DEFAULT_HEIGHT_WIDTH_RATIO
        set(value) {
            field = value
            requestLayout()
        }

    var showGrid = false
        set(value) {
            field = value
            invalidate()
        }

    var gridColor: Int = DEFAULT_GRID_STROKE_COLOR
        set(value) {
            field = value
            gridPaint.color = field
            invalidate()
        }

    var gridWidth: Float = DEFAULT_GRID_STROKE_WIDTH
        set(value) {
            field = value
            gridPaint.strokeWidth = field
            invalidate()
        }

    var lineCount: Int = VERTICAL_GRID_AMOUNT
        set(value) {
            if (value < 1) {
                return
            }

            field = value
            invalidate()
        }

    var watermarkBitmap: Bitmap? = null
        set(value) {
            field = value?.copy(Bitmap.Config.ARGB_8888, true)
            invalidate()
        }


    /* Draw */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = DEFAULT_WIDTH_PIXEL
        val height = (width * heightWidthRatio).toInt()

        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawGrid(canvas)

        drawWatermark(canvas)
    }


    private fun drawGrid(canvas: Canvas) {
        if (!showGrid) {
            return
        }

        var i = 0
        while (i < height) {
            canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), gridPaint)
            i += width / lineCount
        }

        i = 0
        while (i < width) {
            canvas.drawLine(i.toFloat(), 0f, i.toFloat(), height.toFloat(), gridPaint)
            i += width / lineCount
        }
    }

    private fun drawWatermark(canvas: Canvas) {
        watermarkBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, watermarkPaint)
        }
    }


    /* Constructors */
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        context?.obtainStyledAttributes(attrs, R.styleable.GridView)?.apply {
            try {
                showGrid = getBoolean(R.styleable.GridView_showGrid, false)
                gridColor = getColor(R.styleable.GridView_gridColor, DEFAULT_GRID_STROKE_COLOR)
                gridWidth = getDimension(R.styleable.GridView_gridWidth, DEFAULT_GRID_STROKE_WIDTH)
                lineCount = getInt(R.styleable.GridView_lineCount, VERTICAL_GRID_AMOUNT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            recycle()
        }
    }
}