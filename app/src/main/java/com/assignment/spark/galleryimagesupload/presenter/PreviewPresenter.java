package com.assignment.spark.galleryimagesupload.presenter;

import android.graphics.Bitmap; /**
 * Interface that acts as presenter
 */
public interface PreviewPresenter<V> {

    void attachedView(V view);

    void setPreviewLayout();

    void onAcceptCropBtnClick();

    void saveEnhancedBitmap();

    void onRotateBtnClick();

    void setCropLayout();
}