package com.assignment.spark.galleryimagesupload.presenter;

import android.net.Uri;

import java.io.File;

/**
 * Interface that acts as presenter
 */
public interface Presenter <V> {

    void attachedView(V view);

    void onPhotoClicked();

    void onResume(int currentPage);

    void onItemSelected(int position);

    void loadNext(int currentPage);

    void onCaptureBtnClick();

    void permissionDenied();

    void onGalleryBtnClick();
}