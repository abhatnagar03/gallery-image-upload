package com.assignment.spark.galleryimagesupload.presenter;


import com.assignment.spark.galleryimagesupload.view.MainGalleryView;

import java.io.File;

/**
 * Class that defines presenter details
 */

public class MainPresenterImpl implements MainPresenter<MainGalleryView> {

    private MainGalleryView mainGalleryView;

    public MainPresenterImpl() {
    }

    @Override
    public void attachedView(MainGalleryView view) {
        if (view == null)
            throw new IllegalArgumentException("You can't set a null view");

        mainGalleryView = view;
    }

    @Override
    public void onPhotoClicked() {
        mainGalleryView.navigateToPreviewActivity();
    }

    @Override
    public void onItemSelected(int position) {
        mainGalleryView.showDetails(position);
    }

    @Override
    public void onCaptureBtnClick() {

        if (!mainGalleryView.checkPermission()) {
            mainGalleryView.showPermissionDialog();
            return;
        }

        if (!mainGalleryView.isStorageMounted()) {
            mainGalleryView.showDisconnectFromPCDialog();
            return;
        }

        if (mainGalleryView.availableDisk() <= 5) {
            mainGalleryView.showNoSpaceDialog();
            return;
        }

        File file = mainGalleryView.newFile();

        if (file == null) {
            mainGalleryView.showErrorDialog();
            return;
        }

        mainGalleryView.openDeviceCamera(file);
    }

    @Override
    public void permissionDenied() {
        if (mainGalleryView.shouldShowDialog()) mainGalleryView.showPermissionDialog();
        else mainGalleryView.showUnlockPermissionsDialog();
    }

    @Override
    public void onGalleryBtnClick() {
        if (!mainGalleryView.checkGalleryPermission()) {
            mainGalleryView.showGalleryPermissionDialog();
            return;
        }

        File file = mainGalleryView.newFile();

        if (file == null) {
            mainGalleryView.showErrorDialog();
            return;
        }

        mainGalleryView.openDeviceGallery(file);
    }

    @Override
    public void galleryPermissionDenied() {
        if (mainGalleryView.shouldShowDialog()) mainGalleryView.showGalleryPermissionDialog();
        else mainGalleryView.showUnlockGalleryPermissionsDialog();
    }

    @Override
    public void loadInitialData() {
        mainGalleryView.showGallery();
    }
}