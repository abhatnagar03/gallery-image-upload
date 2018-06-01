package com.assignment.spark.galleryimagesupload.model;

/**
 * Interface that loads data
 */
interface DataInteractor {
    void loadItems(int currentPage, DataLoaderListener loaderListener);
}