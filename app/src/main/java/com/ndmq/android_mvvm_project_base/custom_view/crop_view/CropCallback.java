package com.ndmq.android_mvvm_project_base.custom_view.crop_view;

import android.graphics.Bitmap;

public interface CropCallback extends Callback {
  void onSuccess(Bitmap cropped);
}
