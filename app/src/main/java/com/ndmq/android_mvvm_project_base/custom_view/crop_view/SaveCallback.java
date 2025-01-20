package com.ndmq.android_mvvm_project_base.custom_view.crop_view;

import android.net.Uri;

public interface SaveCallback extends Callback {
  void onSuccess(Uri uri);
}
