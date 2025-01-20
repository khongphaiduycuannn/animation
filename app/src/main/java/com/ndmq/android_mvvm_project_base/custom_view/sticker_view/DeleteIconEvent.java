package com.ndmq.android_mvvm_project_base.custom_view.sticker_view;

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public class DeleteIconEvent implements StickerIconEvent {
  @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionMove(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
    stickerView.removeCurrentSticker();
  }
}
