package com.assignment.spark.galleryimagesupload.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.assignment.spark.galleryimagesupload.R;
import com.assignment.spark.galleryimagesupload.crop.PolygonView;
import com.assignment.spark.galleryimagesupload.utils.CameraUtils;
import com.assignment.spark.galleryimagesupload.utils.Constants;

import org.opencv.core.Point;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreviewActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        setPreviewLayout();
    }

    private void setPreviewLayout() {
        Bundle b = getIntent().getExtras();
        mCurrentPhotoPath = b.getString(Constants.URI);

        try {
            preview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    preview.getViewTreeObserver().removeOnGlobalLayoutListener(
                            this);
                    bmp = setPic();
                    if (!("".equals(bmp))) {

                        int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
                        int width = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();

                        bmp = resize(bmp, width, height, 0);

                        Map<Integer, PointF> pointFs = new HashMap<>();

                        ArrayList<PointF> points;


                        points = getPolygonDefaultPoints();


                        int index = -1;
                        for (PointF pointF : points) {
                            pointFs.put(++index, pointF);
                        }

                        polygonView.setPoints(pointFs);
                        int padding = (int) getResources().getDimension(R.dimen.scan_padding);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bmp.getWidth() + 2 * padding, bmp.getHeight() + 2 * padding);
                        layoutParams.gravity = Gravity.CENTER;
                        polygonView.setLayoutParams(layoutParams);

                        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        preview.setImageBitmap(bmp);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
//            saveEnhancedBitmap(previewBitmap);
    }

    private Bitmap setPic() {
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

    @NonNull
    private ArrayList<PointF> getPolygonDefaultPoints() {
        ArrayList<PointF> points;
        points = new ArrayList<>();
        points.add(new PointF(bmp.getWidth() * (0.09f), (float) bmp.getHeight() * (0.27f)));
        points.add(new PointF(bmp.getWidth() * (0.91f), (float) bmp.getHeight() * (0.27f)));
        points.add(new PointF(bmp.getWidth() * (0.09f), (float) bmp.getHeight() * (0.73f)));
        points.add(new PointF(bmp.getWidth() * (0.91f), (float) bmp.getHeight() * (0.73f)));

        return points;
    }

    private Bitmap resize(Bitmap bm, int newWidth, int newHeight, int angle) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        if (angle > 0) {
            matrix.postRotate(angle);
        }

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @OnClick(R.id.accept_btn)
    public void onCropAcceptBtnClick() {
        Map<Integer, PointF> points = polygonView.getPoints();

        Bitmap croppedBitmap;

        if (isScanPointsValid(points)) {
            Point point1 = new Point(points.get(0).x, points.get(0).y);
            Point point2 = new Point(points.get(1).x, points.get(1).y);
            Point point3 = new Point(points.get(2).x, points.get(2).y);
            Point point4 = new Point(points.get(3).x, points.get(3).y);
            croppedBitmap = CameraUtils.enhanceReceipt(bmp, point1, point2, point3, point4);
        } else {
            croppedBitmap = bmp;
        }

        saveEnhancedBitmap(croppedBitmap);
    }

    private void saveEnhancedBitmap(Bitmap croppedBitmap) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mCurrentPhotoPath);
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    @OnClick(R.id.rotate_btn)
    public void rotateImage() {
        rotateAngle = rotateAngle >= 360 ? 90 : rotateAngle + 90;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
        int width = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();

        if (rotateAngle == 90 || rotateAngle == 270) {
            bmp = resize(bmp, width, height, 90);
        } else {
            bmp = resize(bmp, height, width, 90);
        }
        preview.setImageBitmap(bmp);
    }
}
