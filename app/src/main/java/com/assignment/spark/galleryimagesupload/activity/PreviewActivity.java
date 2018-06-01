package com.assignment.spark.galleryimagesupload.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.utils.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity {

    @Bind(R.id.preview)
    ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        setPreviewLayout();
    }

    private void setPreviewLayout() {
        Bundle b = getIntent().getExtras();
        Uri uri = b.getParcelable(Constants.URI);
        Glide
                .with(this)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade()
                .error(R.drawable.placeholder)
                .into(preview);

    }
}
