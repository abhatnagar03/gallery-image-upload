package com.assignment.spark.galleryimagesupload.presenter;

import android.net.Uri;

import java.io.File;

/**
 * Interface that acts as presenter
 */
public interface MainPresenter<V> {

    void attachedView(V view);

    void onPhotoClicked();

    void onCaptureBtnClick();

    void permissionDenied();

    void onGalleryBtnClick();

    void galleryPermissionDenied();

    void loadInitialData();

    void onItemSelected(int position);
}