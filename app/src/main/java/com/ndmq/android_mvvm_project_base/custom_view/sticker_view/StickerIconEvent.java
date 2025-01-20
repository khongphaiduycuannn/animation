package com.ndmq.android_mvvm_project_base.custom_view.sticker_view;

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public interface StickerIconEvent {
  void onActionDown(StickerView stickerView, MotionEvent event);

  void onActionMove(StickerView stickerView, MotionEvent event);

  void onActionUp(StickerView stickerView, MotionEvent event);
}
