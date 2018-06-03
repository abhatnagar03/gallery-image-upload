package com.assignment.spark.galleryimagesupload.presenter;

import android.graphics.Bitmap;
import com.assignment.spark.galleryimagesupload.view.PreviewView;

/**
 * Class that defines presenter details
 */

public class PreviewPresenterImpl implements PreviewPresenter<PreviewView> {

    private PreviewView previewView;

    public PreviewPresenterImpl() {

    }

    @Override
    public void attachedView(PreviewView view) {
        if (view == null)
            throw new IllegalArgumentException("You can't set a null view");

        previewView = view;
    }

    @Override
    public void setPreviewLayout() {
        previewView.getFilePath();
        previewView.getBitmapFromFilePath();
    }

    @Override
    public void setCropLayout() {
        previewView.setCropHandlers();
        previewView.setPreviewImage();
    }

    @Override
    public void onAcceptCropBtnClick() {
        previewView.cropImage();
    }

    @Override
    public void saveEnhancedBitmap() {
        previewView.saveImage();
    }

    @Override
    public void onRotateBtnClick() {
        previewView.rotateBmp();
    }
}