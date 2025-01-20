package com.ndmq.android_mvvm_project_base.custom_view.draw_view.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.LinkedList;
import java.util.Queue;

public class QueueLinearFloodFiller {

    protected Bitmap image = null;
    protected int width = 0;
    protected int height = 0;
    protected int[] pixels = null;
    protected int fillColor = 0;
    protected int[] startColor = new int[]{0, 0, 0, 0}; // Thêm alpha
    protected int[] tolerance = new int[]{0, 0, 0, 0};
    protected boolean[] pixelsChecked;
    protected Queue<FloodFillRange> ranges;


    public QueueLinearFloodFiller(Bitmap img) {
        copyImage(img);
    }

    public QueueLinearFloodFiller(Bitmap img, int targetColor, int newColor) {
        useImage(img);

        setFillColor(newColor);
        setTargetColor(targetColor);
    }

    public void setTargetColor(int targetColor) {
        startColor[0] = Color.alpha(targetColor);
        startColor[1] = Color.red(targetColor);
        startColor[2] = Color.green(targetColor);
        startColor[3] = Color.blue(targetColor);
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int value) {
        fillColor = value;
    }

    public int[] getTolerance() {
        return tolerance;
    }

    public void setTolerance(int[] value) {
        tolerance = value;
    }

    public void setTolerance(int value) {
        tolerance = new int[]{value, value, value, value};
    }

    public Bitmap getImage() {
        return image;
    }

    public void copyImage(Bitmap img) {
        width = img.getWidth();
        height = img.getHeight();

        image = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(image);
        canvas.drawBitmap(img, 0, 0, null);

        pixels = new int[width * height];

        image.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }

    public void useImage(Bitmap img) {
        width = img.getWidth();
        height = img.getHeight();
        image = img;

        pixels = new int[width * height];

        image.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }

    protected void prepare() {
        pixelsChecked = new boolean[pixels.length];
        ranges = new LinkedList<FloodFillRange>();
    }

    public void floodFill(int x, int y) {
        // Setup
        prepare();

        if (startColor[0] == 0) {
            // ***Get starting color.
            int startPixel = pixels[(width * y) + x];
            startColor[0] = (startPixel >> 16) & 0xff;
            startColor[1] = (startPixel >> 8) & 0xff;
            startColor[2] = startPixel & 0xff;
        }

        LinearFill(x, y);

        FloodFillRange range;

        while (ranges.size() > 0) {
            range = ranges.remove();

            int downPxIdx = (width * (range.Y + 1)) + range.startX;
            int upPxIdx = (width * (range.Y - 1)) + range.startX;
            int upY = range.Y - 1;
            int downY = range.Y + 1;

            for (int i = range.startX; i <= range.endX; i++) {
                if (range.Y > 0 && (!pixelsChecked[upPxIdx])
                        && CheckPixel(upPxIdx))
                    LinearFill(i, upY);

                if (range.Y < (height - 1) && (!pixelsChecked[downPxIdx])
                        && CheckPixel(downPxIdx))
                    LinearFill(i, downY);

                downPxIdx++;
                upPxIdx++;
            }
        }

        image.setPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }


    protected void LinearFill(int x, int y) {
        int lFillLoc = x;
        int pxIdx = (width * y) + x;

        while (true) {
            pixels[pxIdx] = fillColor;

            pixelsChecked[pxIdx] = true;

            lFillLoc--;
            pxIdx--;

            if (lFillLoc < 0 || (pixelsChecked[pxIdx]) || !CheckPixel(pxIdx)) {
                break;
            }
        }

        lFillLoc++;

        int rFillLoc = x;

        pxIdx = (width * y) + x;

        while (true) {
            pixels[pxIdx] = fillColor;

            pixelsChecked[pxIdx] = true;

            rFillLoc++;
            pxIdx++;

            if (rFillLoc >= width || pixelsChecked[pxIdx] || !CheckPixel(pxIdx)) {
                break;
            }
        }

        rFillLoc--;

        FloodFillRange r = new FloodFillRange(lFillLoc, rFillLoc, y);

        ranges.offer(r);
    }

    protected boolean CheckPixel(int px) {
        int alpha = (pixels[px] >>> 24) & 0xff;
        int red = (pixels[px] >>> 16) & 0xff;
        int green = (pixels[px] >>> 8) & 0xff;
        int blue = pixels[px] & 0xff;

        return (alpha >= (startColor[0] - tolerance[0])
                && alpha <= (startColor[0] + tolerance[0])
                && red >= (startColor[1] - tolerance[1])
                && red <= (startColor[1] + tolerance[1])
                && green >= (startColor[2] - tolerance[2])
                && green <= (startColor[2] + tolerance[2])
                && blue >= (startColor[3] - tolerance[3])
                && blue <= (startColor[3] + tolerance[3]));
    }

    protected class FloodFillRange {
        public int startX;
        public int endX;
        public int Y;

        public FloodFillRange(int startX, int endX, int y) {
            this.startX = startX;
            this.endX = endX;
            this.Y = y;
        }
    }
}
   