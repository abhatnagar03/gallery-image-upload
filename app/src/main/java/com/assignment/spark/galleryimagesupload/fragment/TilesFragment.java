package com.assignment.spark.galleryimagesupload.fragment;


import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.adapter.BaseAdapter;
import com.assignment.spark.galleryimagesupload.layoutmanager.GridAutoFitLayoutManager;
import com.assignment.spark.galleryimagesupload.widget.ItemOffsetDecoration;

import java.io.File;
import java.util.List;

/**
 * Fragment that displays the images
 */
public class TilesFragment extends BaseFragment {

    public static TilesFragment newInstance() {
        return new TilesFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_tiles;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return getGridLayoutManager();
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List<File> itemList) {
        return new BaseAdapter(getActivity(), itemList, R.layout.item_type_main);
    }

    private GridAutoFitLayoutManager getGridLayoutManager() {
        return new GridAutoFitLayoutManager(
                getActivity(),
                150);
    }

    @Override
    protected void setupRecyclerViewProperties() {
        if (getLayoutManager() != null) {
            layoutManager = (GridAutoFitLayoutManager) getLayoutManager();
            recyclerView.setLayoutManager(layoutManager);
        }

        recyclerView.addItemDecoration(new ItemOffsetDecoration(recyclerView.getContext(), R.dimen.item_decoration));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}