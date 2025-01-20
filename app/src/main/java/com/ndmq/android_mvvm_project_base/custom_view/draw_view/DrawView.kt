package com.ndmq.android_mvvm_project_base.custom_view.draw_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ndmq.android_mvvm_project_base.R
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.callback.OnDrawActionInvokeListener
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.config.gson.SerializablePath
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.constants.ViewConstants.DEFAULT_HEIGHT_WIDTH_RATIO
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.constants.ViewConstants.DEFAULT_WIDTH_PIXEL
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.DrawAction
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.QueueLinearFloodFiller
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.model.ShapeType
import com.ndmq.android_mvvm_project_base.data.model.Frame
import kotlin.math.max
import kotlin.math.min


class DrawView : View {

    companion object {

        /* Stroke paint */
        const val MIN_STROKE_WIDTH = 10f

        const val MAX_STROKE_WIDTH = 67f

        const val DEFAULT_STROKE_WIDTH = 15f

        /* Fill const */
        const val FILL_TOLERANCE = 127


        private var stickerMap = mutableMapOf<String, Bitmap>()

        fun loadStickers(data: MutableMap<String, Bitmap>) {
            stickerMap = data
        }
    }

    enum class Mode {
        BRUSH, ERASE, FILL, DROP_COLOR, SHAPE, IMAGE_STICKER, TEXT
    }


    private fun createDefaultPaint(): Paint {
        val paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.isFilterBitmap = true
        paint.isDither = true
        return paint
    }


    /* Paint */
    private val strokePaint = createDefaultPaint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = DEFAULT_STROKE_WIDTH
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val strokePath = SerializablePath()

    private val erasePaint = createDefaultPaint().apply {
        style = Paint.Style.STROKE
        color = Color.TRANSPARENT
        strokeWidth = DEFAULT_STROKE_WIDTH
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val erasePath = SerializablePath()

    private val shapePaint = createDefaultPaint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = DEFAULT_STROKE_WIDTH
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val stickerPaint = createDefaultPaint()


    /* Undo - redo action */
    private var actions = mutableListOf<DrawAction>()

    private var redoActions = mutableListOf<DrawAction>()


    /* Attrs */
    var heightWidthRatio = DEFAULT_HEIGHT_WIDTH_RATIO
        set(value) {
            if (field == value) {
                return
            }

            field = value
            requestLayout()
        }


    private var mBitmap: Bitmap? = null

    private var mCanvas: Canvas? = null


    var frame: Frame
        get() = Frame(actions.toList(), redoActions.toList())
        set(value) {
            actions.clear()
            actions.addAll(value.actions)

            redoActions.clear()
            redoActions.addAll(value.redoActions)

            redrawAction()
        }


    var isLooked = false


    var currMode = Mode.BRUSH

    private var currShape = ShapeType.RECTANGLE


    private var fillColor = Color.BLACK

    private var dropColor: Int? = null


    private var currentSticker: String? = null

    private val srcRect = Rect()

    private val dstRect = Rect()


    private val textPaint = TextPaint()


    private var onDrawActionInvokeListener: OnDrawActionInvokeListener? = null


    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = DEFAULT_WIDTH_PIXEL
        val height = (width * heightWidthRatio).toInt()

        resetBitmap(width, height)

        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        when (currMode) {
            Mode.BRUSH -> canvas.drawPath(strokePath, strokePaint)

            Mode.ERASE -> canvas.drawPath(erasePath, erasePaint)

            Mode.FILL -> {
                /* Do nothing hehe */
            }

            Mode.SHAPE -> drawShape(
                canvas,
                previousShapeX,
                previousShapeY,
                currentShapeX,
                currentShapeY,
                shapePaint
            )

            Mode.IMAGE_STICKER -> drawSticker(
                canvas,
                previousStickerX,
                previousStickerY,
                currentStickerX,
                currentStickerY,
                currentSticker
            )

            Mode.DROP_COLOR -> {

            }

            Mode.TEXT -> {

            }
        }
    }

    private fun drawShape(
        canvas: Canvas,
        sx: Float,
        sy: Float,
        ex: Float,
        ey: Float,
        paint: Paint
    ) {
        if (sx == 0f && sy == 0f && ex == 0f && ey == 0f) {
            return
        }

        when (currShape) {
            ShapeType.OVAL -> {
                canvas.drawOval(sx, sy, ex, ey, paint)
            }

            ShapeType.RECTANGLE -> {
                canvas.drawRect(sx, sy, ex, ey, paint)
            }

            ShapeType.TRIANGLE -> {}
        }
    }

    private fun drawSticker(
        canvas: Canvas,
        sx: Float,
        sy: Float,
        ex: Float,
        ey: Float,
        currSticker: String?
    ) {
        if (currSticker == null) {
            return
        }

        if (sx == 0f && sy == 0f && ex == 0f && ey == 0f) {
            return
        }

        stickerMap[currSticker]?.let { bitmap ->
            val width = bitmap.width
            val height = bitmap.height
            srcRect.set(0, 0, width, height)

            val minSx = min(sx.toInt(), ex.toInt())
            val maxSx = max(sx.toInt(), ex.toInt())
            val minEy = min(sy.toInt(), ey.toInt())
            val maxEy = max(sy.toInt(), ey.toInt())
            dstRect.set(minSx, minEy, maxSx, maxEy)

            canvas.drawBitmap(bitmap, srcRect, dstRect, stickerPaint)
        }
    }

    private fun drawText(action: DrawAction.Text) {
        with(action) {
            textPaint.apply {
                color = action.color
                textSize = action.textSize * scale
            }

            val staticLayout = StaticLayout(
                text,
                textPaint,
                (width * scale).toInt(),
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                0.0f,
                true
            )

            mCanvas?.let { canvas ->
                canvas.save()
                canvas.translate(x, y)
                canvas.rotate(angle)
                staticLayout.draw(canvas)
                canvas.restore()
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isLooked) return false

        when (currMode) {
            Mode.BRUSH, Mode.ERASE -> doOnStrokeOrEraseMode(event)

            Mode.FILL -> doOnFillMode(event)

            Mode.DROP_COLOR -> doOnDropMode(event)

            Mode.SHAPE -> doOnShapeMode(event)

            Mode.IMAGE_STICKER -> doOnStickerMode(event)

            Mode.TEXT -> {}
        }
        return true
    }


    private var previousStrokeX = 0f
    private var previousStrokeY = 0f

    private fun doOnStrokeOrEraseMode(event: MotionEvent) {
        val x = event.x
        val y = event.y
        val path = if (currMode == Mode.BRUSH) strokePath else erasePath
        val paint = if (currMode == Mode.BRUSH) strokePaint else erasePaint

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                previousStrokeX = x
                previousStrokeY = y
            }

            MotionEvent.ACTION_MOVE -> {
                val controlX = (previousStrokeX + x) / 2
                val controlY = (previousStrokeY + y) / 2
                path.quadTo(previousStrokeX, previousStrokeY, controlX, controlY)
                previousStrokeX = x
                previousStrokeY = y
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                path.lineTo(x, y)
                mCanvas?.drawPath(path, paint)

                redoActions.clear()
                if (currMode == Mode.BRUSH) {
                    val action = DrawAction.Brush(SerializablePath(path), Paint(paint))
                    actions.add(action)

                    onDrawActionInvokeListener?.onDrawActionInvoke(action)
                    onDrawActionInvokeListener?.onStrokeActionInvoke(action)
                } else {
                    val action = DrawAction.Erase(SerializablePath(path))
                    actions.add(action)

                    onDrawActionInvokeListener?.onDrawActionInvoke(action)
                    onDrawActionInvokeListener?.onEraseActionInvoke(action)
                }

                path.reset()
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                path.reset()
                invalidate()
            }
        }
    }


    private fun doOnFillMode(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                mBitmap?.let { bitmap ->
                    val targetColor = bitmap.getPixel(x, y)
                    val floodFiller = QueueLinearFloodFiller(
                        bitmap,
                        targetColor,
                        fillColor
                    ).apply {
                        setTolerance(FILL_TOLERANCE)
                    }
                    floodFiller.floodFill(x, y)
                }
                redoActions.clear()
                actions.add(DrawAction.Fill(x, y, fillColor))
                invalidate()

                onDrawActionInvokeListener?.onDrawActionInvoke(DrawAction.Fill(x, y, fillColor))
                onDrawActionInvokeListener?.onFillActionInvoke(DrawAction.Fill(x, y, fillColor))
            }
        }
    }


    private fun doOnDropMode(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mBitmap?.getPixel(x, y)?.let {
                    dropColor = it
                    onDrawActionInvokeListener?.onDropColorActionInvoke(it)
                }
            }
        }
    }


    private var previousShapeX = 0f
    private var previousShapeY = 0f
    private var currentShapeX = 0f
    private var currentShapeY = 0f

    private fun doOnShapeMode(event: MotionEvent) {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousShapeX = x
                previousShapeY = y
            }

            MotionEvent.ACTION_MOVE -> {
                currentShapeX = event.x
                currentShapeY = event.y
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                mCanvas?.let {
                    drawShape(
                        it,
                        previousShapeX,
                        previousShapeY,
                        currentShapeX,
                        currentShapeY,
                        Paint(shapePaint)
                    )
                }
                redoActions.clear()

                val action = DrawAction.Shape(
                    previousShapeX,
                    previousShapeY,
                    currentShapeX,
                    currentShapeY,
                    currShape,
                    Paint(shapePaint)
                )
                actions.add(action)

                previousShapeX = 0f
                previousShapeY = 0f
                currentShapeX = 0f
                currentShapeY = 0f
                invalidate()

                onDrawActionInvokeListener?.onDrawActionInvoke(action)
                onDrawActionInvokeListener?.onShapeActionInvoke(action)
            }

            MotionEvent.ACTION_CANCEL -> {
                previousShapeX = 0f
                previousShapeY = 0f
                currentShapeX = 0f
                currentShapeY = 0f
                invalidate()
            }
        }
    }


    private var previousStickerX = 0f
    private var previousStickerY = 0f
    private var currentStickerX = 0f
    private var currentStickerY = 0f

    private fun doOnStickerMode(event: MotionEvent) {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                previousStickerX = x
                previousStickerY = y
            }

            MotionEvent.ACTION_MOVE -> {
                currentStickerX = event.x
                currentStickerY = event.y
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (currentSticker != null) {
                    mCanvas?.let {
                        drawSticker(
                            it,
                            previousStickerX,
                            previousStickerY,
                            currentStickerX,
                            currentStickerY,
                            currentSticker
                        )
                    }
                    redoActions.clear()

                    val action = DrawAction.ImageSticker(
                        previousStickerX,
                        previousStickerY,
                        currentStickerX,
                        currentStickerY,
                        currentSticker!!
                    )
                    actions.add(action)

                    onDrawActionInvokeListener?.onDrawActionInvoke(action)
                    onDrawActionInvokeListener?.onStickerActionInvoke(action)
                }

                previousStickerX = 0f
                previousStickerY = 0f
                currentStickerX = 0f
                currentStickerY = 0f
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                previousStickerX = 0f
                previousStickerY = 0f
                currentStickerX = 0f
                currentStickerY = 0f
                invalidate()
            }
        }
    }


    fun addText(text: DrawAction.Text) {
        drawText(text)
        actions.add(text)
        redoActions.clear()
        invalidate()

        onDrawActionInvokeListener?.onDrawActionInvoke(text)
        onDrawActionInvokeListener?.onTextActionInvoke(text)
    }


    fun undo() {
        if (actions.isEmpty()) {
            return
        }

        val last = actions.last()
        redoActions.add(last)
        actions.removeLast()

        resetBitmap(width, height)
        if (mCanvas != null && mBitmap != null) {
            drawAction(mCanvas!!, mBitmap!!)
        }

        invalidate()

        onDrawActionInvokeListener?.onDrawActionInvoke()
        onDrawActionInvokeListener?.onUndoActionInvoke()
    }

    fun redo() {
        if (redoActions.isEmpty()) {
            return
        }

        val last = redoActions.last()
        redoActions.removeLast()
        actions.add(last)

        resetBitmap(width, height)
        if (mCanvas != null && mBitmap != null) {
            drawAction(mCanvas!!, mBitmap!!)
        }

        invalidate()

        onDrawActionInvokeListener?.onDrawActionInvoke()
        onDrawActionInvokeListener?.onRedoActionInvoke()
    }

    private fun drawAction(canvas: Canvas, bitmap: Bitmap) {
        actions.forEach { action ->
            when (action) {
                is DrawAction.Brush -> {
                    canvas.drawPath(action.path, action.paint)
                }

                is DrawAction.Erase -> {
                    canvas.drawPath(action.path, erasePaint)
                }

                is DrawAction.Fill -> {
                    val targetColor = bitmap.getPixel(action.x, action.y)
                    val floodFiller =
                        QueueLinearFloodFiller(
                            bitmap,
                            targetColor,
                            action.color
                        ).apply {
                            setTolerance(FILL_TOLERANCE)
                        }
                    floodFiller.floodFill(action.x, action.y)
                }

                is DrawAction.Shape -> drawShape(
                    canvas,
                    action.startX,
                    action.startY,
                    action.endX,
                    action.endY,
                    action.paint
                )

                is DrawAction.ImageSticker -> drawSticker(
                    canvas,
                    action.startX,
                    action.startY,
                    action.endX,
                    action.endY,
                    action.path
                )

                is DrawAction.Text -> drawText(action)
            }
        }
    }


    fun setOnDrawActionInvokeListener(listener: OnDrawActionInvokeListener) {
        onDrawActionInvokeListener = listener
    }


    private fun resetBitmap(width: Int, height: Int) {
        if (width <= 0 || height <= 0) {
            return
        }

        if (mBitmap != null) {
            mBitmap?.recycle()
            mBitmap = null
        }

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mBitmap?.let { mCanvas = Canvas(it) }
    }

    fun setBitmapWidthHeight(width: Int, height: Int) {
        resetBitmap(width, height)
    }

    fun redrawAction() {
        resetBitmap(width, height)
        if (mCanvas != null && mBitmap != null) {
            drawAction(mCanvas!!, mBitmap!!)
        }
        invalidate()
    }


    fun setBrushColor(color: Int) {
        strokePaint.color = color
    }

    fun setBrushWidth(width: Float) {
        if (width < MIN_STROKE_WIDTH || width > MAX_STROKE_WIDTH) {
            return
        }

        strokePaint.strokeWidth = width
    }

    fun setEraseWidth(width: Float) {
        if (width < MIN_STROKE_WIDTH || width > MAX_STROKE_WIDTH) {
            return
        }

        erasePaint.strokeWidth = width
    }

    fun setFillColor(color: Int) {
        fillColor = color
    }

    fun setShapeColor(color: Int) {
        shapePaint.color = color
    }

    fun setShapeWidth(width: Float) {
        if (width < MIN_STROKE_WIDTH || width > MAX_STROKE_WIDTH) {
            return
        }

        shapePaint.strokeWidth = width
    }

    fun setCurrentShape(shape: Int) {
        currShape = shape
    }

    fun setCurrentSticker(sticker: String) {
        currentSticker = sticker
    }

    fun getDrawBitmap() = mBitmap

    fun recycle() {
        mBitmap?.recycle()
        mBitmap = null
        mCanvas = null
    }


    /* Constructors */
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        context?.obtainStyledAttributes(attrs, R.styleable.DrawView)?.apply {
            try {
                isLooked = getBoolean(R.styleable.DrawView_isLocked, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            recycle()
        }
    }
}