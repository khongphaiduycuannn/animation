package com.ndmq.android_mvvm_project_base.custom_view.crop_view;

public interface SimpleValueAnimatorListener {
  void onAnimationStarted();

  void onAnimationUpdated(float scale);

  void onAnimationFinished();
}
