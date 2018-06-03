package com.assignment.spark.galleryimagesupload.view;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

public class Quadrilateral {
    public MatOfPoint2f contour;
    public Point[] points;

    public Quadrilateral(MatOfPoint2f contour, Point[] points) {
        this.contour = contour;
        this.points = points;
    }
}