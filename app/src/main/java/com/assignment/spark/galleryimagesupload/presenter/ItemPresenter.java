package com.assignment.spark.galleryimagesupload.presenter;


import android.net.Uri;

import com.assignment.spark.galleryimagesupload.model.DataLoaderListener;
import com.assignment.spark.galleryimagesupload.view.ItemView;

import java.io.File;

/**
 * Class that defines presenter details
 */

public class ItemPresenter implements Presenter<ItemView> {

    private ItemView itemView;
//    private final ItemInteractor itemInteractor;

    public ItemPresenter() {
    }

    @Override
    public void attachedView(ItemView view) {
        if (view == null)
        throw new IllegalArgumentException("You can't set a null view");

        itemView = view;
    }

    @Override
    public void detachView() {
        itemView = null;
    }

    @Override
    public void onResume(int currentPage) {
        itemView.showProgress();
//        itemInteractor.loadItems(currentPage, this);
    }

    @Override
    public void onItemSelected(int position) {
        itemView.showDetails(position);
    }

    @Override
    public void loadNext(int currentPage) {
//        itemInteractor.loadItems(currentPage, this);
    }

    @Override
    public void onCaptureBtnClick() {

        if(!itemView.checkPermission()) {
            itemView.showPermissionDialog();
            return;
        }

        if(!itemView.isStorageMounted()) {
            itemView.showDisconnectFromPCDialog();
            return;
        }

        if(itemView.availableDisk() <= 5) {
            itemView.showNoSpaceDialog();
            return;
        }

        File file = itemView.newFile();

        if(file == null) {
            itemView.showErrorDialog();
            return;
        }

        dispatchTakePictureIntent(file);
    }

    private void dispatchTakePictureIntent(File file) {
        itemView.openDeviceCamera(file);
    }

//    @Override
//    public void onInitialItemsLoaded(ItemList itemList) {
//        itemView.hideProgress();
//        itemView.resetRefreshingLayout();
//    }
//
//    @Override
//    public void onNextItemsLoaded(ItemList itemList) {
//        itemView.resetRefreshingLayout();
//    }

    @Override
    public void permissionDenied() {
        if(itemView.shouldShowDialog()) itemView.showPermissionDialog();
        else itemView.showUnlockPermissionsDialog();
    }

    @Override
    public void saveImage(Uri uri) {

    }
}