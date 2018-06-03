package com.assignment.spark.galleryimagesupload.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraUtils {
    private static final String TAG = CameraUtils.class.getSimpleName();

    public static Bitmap enhanceImage(Bitmap image, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        int resultWidth = (int) (topRight.x - topLeft.x);
        int bottomWidth = (int) (bottomRight.x - bottomLeft.x);
        if (bottomWidth > resultWidth)
            resultWidth = bottomWidth;

        int resultHeight = (int) (bottomLeft.y - topLeft.y);
        int bottomHeight = (int) (bottomRight.y - topRight.y);
        if (bottomHeight > resultHeight)
            resultHeight = bottomHeight;

        Mat inputMat = new Mat(image.getHeight(), image.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(image, inputMat);
        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC1);

        List<Point> source = new ArrayList<>();
        source.add(topLeft);
        source.add(topRight);
        source.add(bottomLeft);
        source.add(bottomRight);
        Mat startM = Converters.vector_Point2f_to_Mat(source);

        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(resultWidth, 0);
        Point ocvPOut3 = new Point(0, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, resultHeight);
        List<Point> dest = new ArrayList<>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight));

        Bitmap output = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, output);
        return output;
    }

    public static void saveEnhancedBitmap(Bitmap bitmap, String filePath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
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
    }

    @NonNull
    public static ArrayList<PointF> getPolygonDefaultPoints(Bitmap bmp) {
        ArrayList<PointF> points;
        points = new ArrayList<>();
        points.add(new PointF(bmp.getWidth() * (0.09f), (float) bmp.getHeight() * (0.27f)));
        points.add(new PointF(bmp.getWidth() * (0.91f), (float) bmp.getHeight() * (0.27f)));
        points.add(new PointF(bmp.getWidth() * (0.09f), (float) bmp.getHeight() * (0.73f)));
        points.add(new PointF(bmp.getWidth() * (0.91f), (float) bmp.getHeight() * (0.73f)));

        return points;
    }

    public static Bitmap resize(Bitmap bm, int newWidth, int newHeight, int angle) {
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

    public static boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }
}