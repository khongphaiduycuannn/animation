package com.ndmq.android_mvvm_project_base.custom_view.crop_view;

import android.graphics.Bitmap;
import android.net.Uri;

import io.reactivex.Single;

public class CropRequest {

  private CropView cropImageView;
  private Uri sourceUri;
  private int outputWidth;
  private int outputHeight;
  private int outputMaxWidth;
  private int outputMaxHeight;

  public CropRequest(CropView cropImageView, Uri sourceUri) {
    this.cropImageView = cropImageView;
    this.sourceUri = sourceUri;
  }

  public CropRequest outputWidth(int outputWidth) {
    this.outputWidth = outputWidth;
    this.outputHeight = 0;
    return this;
  }

  public CropRequest outputHeight(int outputHeight) {
    this.outputHeight = outputHeight;
    this.outputWidth = 0;
    return this;
  }

  public CropRequest outputMaxWidth(int outputMaxWidth) {
    this.outputMaxWidth = outputMaxWidth;
    return this;
  }

  public CropRequest outputMaxHeight(int outputMaxHeight) {
    this.outputMaxHeight = outputMaxHeight;
    return this;
  }

  private void build() {
    if (outputWidth > 0) cropImageView.setOutputWidth(outputWidth);
    if (outputHeight > 0) cropImageView.setOutputHeight(outputHeight);
    cropImageView.setOutputMaxSize(outputMaxWidth, outputMaxHeight);
  }

  public void execute(CropCallback cropCallback) {
    build();
    cropImageView.cropAsync(sourceUri, cropCallback);
  }

  public Single<Bitmap> executeAsSingle() {
    build();
    return cropImageView.cropAsSingle(sourceUri);
  }
}
