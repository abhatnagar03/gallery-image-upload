package com.assignment.spark.galleryimagesupload.view;

import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * Interface that provides methods for setting up list data
 */
public interface MainGalleryView {

    void showDetails(int position);

    void openDeviceCamera(File file);

    boolean checkPermission();

    void showPermissionDialog();

    File getEnvFilePath();

    boolean isStorageMounted();

    boolean shouldShowDialog();

    void showUnlockPermissionsDialog();

    void showDisconnectFromPCDialog();

    void showNoSpaceDialog();

    int availableDisk();

    File newFile();

    void showErrorDialog();

    void navigateToPreviewActivity();

    boolean checkGalleryPermission();

    void showGalleryPermissionDialog();

    void openDeviceGallery(File file);

    void showUnlockGalleryPermissionsDialog();

    void showGallery();
}