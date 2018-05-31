package com.assignment.spark.galleryimagesupload.fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.adapter.AdapterExample;

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
        return getLinearLayoutManager();
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List<File> itemList) {
        return new AdapterExample(getActivity(), itemList, R.layout.item_type_main);
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.VERTICAL,
                false);
    }
}