package com.assignment.spark.galleryimagesupload.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.presenter.RecyclerItemClickListener;
import com.assignment.spark.galleryimagesupload.utils.Constants;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter class that binds data to the recycler view
 */
public class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;

    private Context context;
    private List<File> itemList;
    private int itemLayout;
    private RecyclerItemClickListener recyclerItemClickListener;

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public BaseAdapter() {
    }

    public BaseAdapter(Context context, List<File> itemList, int itemLayout) {
        this.context = context;
        this.itemList = itemList;
        this.itemLayout = itemLayout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getItemViewHolder(parent, inflater);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent, LayoutInflater inflater) {
        View view = inflater.inflate(itemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final File item = itemList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder viewHolder = (ViewHolder) holder;
                Glide.with(context).load(FileProvider.getUriForFile(context,
                        Constants.FILE_AUTHORITY, item)).into(viewHolder.imageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    private void add(File r) {
        itemList.add(r);
        notifyItemInserted(itemList.size() - 1);
    }

    public void addAll(List<File> moveResults) {
        for (File result : moveResults) {
            add(result);
        }
    }

    private void remove(File r) {
        int position = itemList.indexOf(r);
        if (position > -1) {
            itemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public File getItem(int position) {
        return itemList.get(position);
    }

   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.imageView)
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (recyclerItemClickListener != null)
                recyclerItemClickListener.onItemClickListener(getAdapterPosition());
        }
    }
}