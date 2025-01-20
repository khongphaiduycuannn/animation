package com.ndmq.android_mvvm_project_base.custom_view.color_picker_view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

final class DrawingUtils {

    static int dpToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
        int res = (int) (val + 0.5);

        return res == 0 && val > 0 ? 1 : res;
    }
}