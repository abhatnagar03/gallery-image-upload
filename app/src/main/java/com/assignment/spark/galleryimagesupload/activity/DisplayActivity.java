package com.assignment.spark.galleryimagesupload.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.utils.CameraUtils;
import com.assignment.spark.galleryimagesupload.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DisplayActivity extends AppCompatActivity {
    private String path;

    @Bind(R.id.preview)
    ImageView preview;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ButterKnife.bind(this);

        getFilePath();
        getBitmapFromFilePath();
    }

    private void getFilePath() {
        Bundle b = getIntent().getExtras();
        path = b.getString(Constants.URI);
    }

    @OnClick(R.id.save_gallery)
    public void downloadImage() {
        String imagePath = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bmp,
                "demo_image",
                "demo_image"
        );

        Toast.makeText(DisplayActivity.this, "Image Saved Successfully at: " + Uri.parse(imagePath), Toast.LENGTH_LONG).show();
    }

    public void getBitmapFromFilePath() {
        try {
            preview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    preview.getViewTreeObserver().removeOnGlobalLayoutListener(
                            this);
                    bmp = getBitmap();
                    preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    preview.setImageBitmap(bmp);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmap() {
        // Get the dimensions of the View

        int targetW = preview.getWidth();
        int targetH = preview.getHeight();
        BitmapFactory.Options bmOptions = CameraUtils.getOptions(targetW, targetH, path);


        return BitmapFactory.decodeFile(path, bmOptions);
    }
}
