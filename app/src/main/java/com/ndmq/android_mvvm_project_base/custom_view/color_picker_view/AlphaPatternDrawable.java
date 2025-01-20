package com.ndmq.android_mvvm_project_base.custom_view.color_picker_view;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class AlphaPatternDrawable extends Drawable {

    private int rectangleSize = 10;

    private final Paint paint = new Paint();
    private final Paint paintWhite = new Paint();
    private final Paint paintGray = new Paint();
    private final Paint backgroundPaint = new Paint();

    private int numRectanglesHorizontal;
    private int numRectanglesVertical;

    private Bitmap bitmap;

    AlphaPatternDrawable(int rectangleSize) {
        this.rectangleSize = rectangleSize;
        paintWhite.setColor(0xFFFFFFFF);
        paintGray.setColor(0xFFCBCBCB);
        paintGray.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        backgroundPaint.setColor(0xFFFFFFFF);
    }

    @Override
    public void draw(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, null, getBounds(), paint);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("Alpha is not supported by this drawable.");
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException("ColorFilter is not supported by this drawable.");
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int height = bounds.height();
        int width = bounds.width();
        numRectanglesHorizontal = (int) Math.ceil((width / rectangleSize));
        numRectanglesVertical = (int) Math.ceil(height / rectangleSize);
        generatePatternBitmap();
    }

    private void generatePatternBitmap() {
        if (getBounds().width() <= 0 || getBounds().height() <= 0) {
            return;
        }

        bitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawRoundRect(0, 0, getBounds().width(), getBounds().height(), (float) getBounds().height() / 2, (float) getBounds().height() / 2, backgroundPaint);

        Rect r = new Rect();
        boolean verticalStartWhite = true;
        for (int i = 0; i <= numRectanglesVertical; i++) {
            boolean isWhite = verticalStartWhite;
            for (int j = 0; j <= numRectanglesHorizontal; j++) {
                r.top = i * rectangleSize;
                r.left = j * rectangleSize;
                r.bottom = r.top + rectangleSize;
                r.right = r.left + rectangleSize;
                canvas.drawRect(r, isWhite ? paintWhite : paintGray);
                isWhite = !isWhite;
            }
            verticalStartWhite = !verticalStartWhite;
        }
    }
}