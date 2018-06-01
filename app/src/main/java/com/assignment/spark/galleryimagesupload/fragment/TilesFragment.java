package com.assignment.spark.galleryimagesupload.fragment;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.adapter.BaseAdapter;

import java.io.File;
import java.util.List;

/**
 * Fragment that displays the list
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

    private GridLayoutManager getGridLayoutManager() {
        return new GridLayoutManager(
                getActivity(),
                2);
    }
}