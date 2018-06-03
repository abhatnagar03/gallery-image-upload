package com.assignment.spark.galleryimagesupload.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.crop.PolygonView;
import com.assignment.spark.galleryimagesupload.presenter.PreviewPresenterImpl;
import com.assignment.spark.galleryimagesupload.utils.CameraUtils;
import com.assignment.spark.galleryimagesupload.utils.Constants;
import com.assignment.spark.galleryimagesupload.view.PreviewView;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreviewActivity extends AppCompatActivity implements PreviewView {

    private static final String mOpenCvLibrary = "opencv_java3";

    @Bind(R.id.preview)
    ImageView preview;

    @Bind(R.id.polygonView)
    PolygonView polygonView;

    static {
        System.loadLibrary(mOpenCvLibrary);
    }

    private String mCurrentPhotoPath;
    private Bitmap bmp;
    private int rotateAngle;
    private PreviewPresenterImpl previewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        previewPresenter = new PreviewPresenterImpl();
        previewPresenter.attachedView(this);

        previewPresenter.setPreviewLayout();
    }

    @OnClick(R.id.crop_btn)
    public void onCropAcceptBtnClick() {
        previewPresenter.onAcceptCropBtnClick();
        finish();
    }

    @OnClick(R.id.accept_btn)
    public void onAcceptBtnClick() {
        finish();
    }

    @OnClick(R.id.rotate_btn)
    public void rotateImage() {
        previewPresenter.onRotateBtnClick();
    }

    @Override
    public Bitmap getBitmapFromFilePath() {
        try {
            preview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    preview.getViewTreeObserver().removeOnGlobalLayoutListener(
                            this);
                    bmp = getBitmap();
                    if (!("".equals(bmp))) {

                        int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
                        int width = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();

                        bmp = CameraUtils.resize(bmp, width, height, 0);

                        previewPresenter.setCropLayout();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }

    private Bitmap getBitmap() {
        // Get the dimensions of the View

        int targetW = preview.getWidth();
        int targetH = preview.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    @Override
    public String getFilePath() {
        Bundle b = getIntent().getExtras();
        mCurrentPhotoPath = b.getString(Constants.URI);
        return mCurrentPhotoPath;
    }

    @Override
    public void setCropHandlers() {
        Map<Integer, PointF> pointFs = new HashMap<>();

        ArrayList<PointF> points;

        points = CameraUtils.getPolygonDefaultPoints(bmp);

        int index = -1;
        for (PointF pointF : points) {
            pointFs.put(++index, pointF);
        }

        polygonView.setPoints(pointFs);
        int padding = (int) getResources().getDimension(R.dimen.scan_padding);
        FrameLayout.LayoutParams layoutParams
                = new FrameLayout.LayoutParams(bmp.getWidth() + 2 * padding,
                bmp.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);
    }

    @Override
    public void setPreviewImage() {
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        preview.setImageBitmap(bmp);
    }

    @Override
    public void cropImage() {
        Map<Integer, PointF> points = polygonView.getPoints();

        Bitmap croppedBitmap;

        if (CameraUtils.isScanPointsValid(points)) {
            Point point1 = new Point(points.get(0).x, points.get(0).y);
            Point point2 = new Point(points.get(1).x, points.get(1).y);
            Point point3 = new Point(points.get(2).x, points.get(2).y);
            Point point4 = new Point(points.get(3).x, points.get(3).y);
            croppedBitmap = CameraUtils.enhanceImage(bmp, point1, point2, point3, point4);
        } else {
            croppedBitmap = bmp;
        }

        CameraUtils.saveEnhancedBitmap(croppedBitmap, mCurrentPhotoPath);
    }

    @Override
    public void saveImage() {

    }

    @Override
    public void rotateBmp() {
        rotateAngle = rotateAngle >= 360 ? 90 : rotateAngle + 90;

        int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
        int width = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();

        if (rotateAngle == 90 || rotateAngle == 270) {
            bmp = CameraUtils.resize(bmp, width, height, 90);
        } else {
            bmp = CameraUtils.resize(bmp, height, width, 90);
        }
        preview.setImageBitmap(bmp);
    }
}
