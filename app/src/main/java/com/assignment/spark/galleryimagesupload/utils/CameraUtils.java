package com.assignment.spark.galleryimagesupload.utils;

import android.graphics.Bitmap;
import android.util.Log;


import com.assignment.spark.galleryimagesupload.view.Quadrilateral;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;

public class CameraUtils {
    private static final String TAG = CameraUtils.class.getSimpleName();

    public static Quadrilateral detectLargestQuadrilateral(Mat mat) {
        Mat mGrayMat = new Mat(mat.rows(), mat.cols(), CV_8UC1);
        Imgproc.cvtColor(mat, mGrayMat, Imgproc.COLOR_BGR2GRAY, 4);
        Imgproc.threshold(mGrayMat, mGrayMat, 150, 255, THRESH_BINARY + THRESH_OTSU);

        List<MatOfPoint> largestContour = findLargestContour(mGrayMat);
        if(null != largestContour) {
            Quadrilateral mLargestRect = findQuadrilateral(largestContour);
            if (mLargestRect != null)
                return mLargestRect;
        }
        return null;
    }

    public static double getMaxCosine(double maxCosine, Point[] approxPoints) {
        Log.i(TAG, "ANGLES ARE:");
        for (int i = 2; i < 5; i++) {
            double cosine = Math.abs(angle(approxPoints[i % 4], approxPoints[i - 2], approxPoints[i - 1]));
            Log.i(TAG, String.valueOf(cosine));
            maxCosine = Math.max(cosine, maxCosine);
        }
        return maxCosine;
    }

    public static double angle(Point p1, Point p2, Point p0) {
        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dx2 = p2.x - p0.x;
        double dy2 = p2.y - p0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    private static Point[] sortPoints(Point[] src ) {
        ArrayList<Point> srcPoints = new ArrayList<>(Arrays.asList(src));
        Point[] result = { null , null , null , null };

        Comparator<Point> sumComparator = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y + lhs.x).compareTo(rhs.y + rhs.x);
            }
        };

        Comparator<Point> diffComparator = new Comparator<Point>() {

            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y - lhs.x).compareTo(rhs.y - rhs.x);
            }
        };

        // top-left corner = minimal sum
        result[0] = Collections.min(srcPoints, sumComparator);
        // bottom-right corner = maximal sum
        result[2] = Collections.max(srcPoints, sumComparator);
        // top-right corner = minimal diference
        result[1] = Collections.min(srcPoints, diffComparator);
        // bottom-left corner = maximal diference
        result[3] = Collections.max(srcPoints, diffComparator);

        return result;
    }

    private static List<MatOfPoint> findLargestContour(Mat inputMat) {
        Mat mHierarchy = new Mat();
        List<MatOfPoint> mContourList = new ArrayList<>();
        //finding contours
        Imgproc.findContours(inputMat, mContourList, mHierarchy, Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE);

        Mat mContoursMat = new Mat();
        mContoursMat.create(inputMat.rows(), inputMat.cols(), CvType.CV_8U);

        if(mContourList.size() != 0) {
            Collections.sort(mContourList, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                    return Double.valueOf(Imgproc.contourArea(rhs)).compareTo(Imgproc.contourArea(lhs));
                }
            });
            return mContourList;
        }
        return null;
    }

    private static Quadrilateral findQuadrilateral(List<MatOfPoint> mContourList) {
        for ( MatOfPoint c: mContourList ) {
            MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
            double peri = Imgproc.arcLength(c2f, true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true);
            Point[] points = approx.toArray();
            // select biggest 4 angles polygon
            if (approx.rows() == 4) {
                Point[] foundPoints = sortPoints(points);
                return new Quadrilateral( approx , foundPoints);
            }
        }
        return null;
    }

    public static Bitmap enhanceReceipt(Bitmap image, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
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
}