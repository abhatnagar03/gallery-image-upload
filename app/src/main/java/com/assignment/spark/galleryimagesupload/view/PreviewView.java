package com.assignment.spark.galleryimagesupload.view;

import android.graphics.Bitmap;

/**
 * Interface that provides methods for setting up list data
 */
public interface PreviewView {
    Bitmap getBitmapFromFilePath();

    String getFilePath();

    void setCropHandlers();

    void setPreviewImage();

    void cropImage();

    void saveImage();

    void rotateBmp();
}