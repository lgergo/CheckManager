package com.yevsp8.checkmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;
import com.yevsp8.checkmanager.di.TessTwoModule;
import com.yevsp8.checkmanager.util.Constants;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 03. 04..
 */

public class ImageProcessor {

    @Inject
    TessTwoApi tessTwoApi;
    String checkIdResult;
    String amountResult;
    String paidToResult;
    String imagePath;
    Bitmap bitmap;
    Bitmap toTestOnly;
    Mat src;
    Mat corrected;
    Mat dest;

    public ImageProcessor(Context context) {
        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .tessTwoModule(new TessTwoModule(context))
                .build();
        component.injectImageProcessor(this);

        try {
            OpenCVLoader.initDebug();
        } catch (Exception ex) {
            Log.e("e", ex.getMessage());
        }
    }

    public Bitmap preProcessing(Bitmap rawBitmap, String filePath) {
        Log.e("Preprocess", "PREPROCESSING STARTED");
        bitmap = rawBitmap;
        imagePath = filePath;

        src = new Mat(rawBitmap.getHeight(), rawBitmap.getWidth(), CvType.CV_8UC1);
        corrected = new Mat(rawBitmap.getHeight(), rawBitmap.getWidth(), CvType.CV_8UC1);
        dest = new Mat(rawBitmap.getHeight(), rawBitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(rawBitmap, src);
        gammaCorrection();
        src = dest;
        Imgproc.cvtColor(src, dest, Imgproc.COLOR_BGR2GRAY);
        src = dest;
        Imgproc.GaussianBlur(src, dest, new Size(7, 7), 0);
        src = dest;
        Imgproc.equalizeHist(src, dest);
        src = dest;
        Imgproc.threshold(src, dest, getMatMean(), 256, Imgproc.THRESH_BINARY);
        src = dest;

        //---------ez így jó hogy meghatározzuk hol a csekk

        //Mat contoured = drawContours();
        corrected = correctPerspective(src);

//        Bitmap output = Bitmap.createBitmap(corrected.cols(), corrected.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(corrected, output);

        //---------------------
        Mat rect = getRectOfCheckId(corrected);
        Core.bitwise_not(rect, rect);
        Imgproc.GaussianBlur(rect, rect, new Size(9, 9), 1);
        Bitmap output = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rect, output);
        toTestOnly = output;
        //-------------------------

        Log.e("Preprocess", "PREPROCESSING ENDED");
        return output;
    }

    public String[] recognition() {

        String reuslt = tessTwoApi.startRecognition(toTestOnly);
        return new String[]{reuslt, "-", "-"};

//        //TODO külön szálon fusson
//        Mat rect= getRectOfCheckId(corrected);
//        adaptiveThreshold(rect,rect);
//        Bitmap rectBitmapTemp=Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(rect, rectBitmapTemp);
//        checkIdResult=tessTwoApi.startRecognition(rectBitmapTemp);
//
//        rect= getRectOfAmount(corrected);
//        rectBitmapTemp=Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(rect, rectBitmapTemp);
//        amountResult=tessTwoApi.startRecognition(rectBitmapTemp);
//
//        rect= getRectOfPaidTo(corrected);
//        rectBitmapTemp=Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(rect, rectBitmapTemp);
//        paidToResult=tessTwoApi.startRecognition(rectBitmapTemp);
//
//        return new String[]{checkIdResult,amountResult,paidToResult};
    }


//    private void loadImage() {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = false;
//        //TODO for demo data only
//        if (imagePath.contains("JPEG_")) {
//            options.inSampleSize = 8;  //higher is smaller image
//        }
//        bitmap = BitmapFactory.decodeFile(imagePath, options);
//    }

    private void gammaCorrection() {
        double gamma = 2.0;
        Mat lut = new Mat(1, 256, CvType.CV_8UC1);
        for (int i = 0; i < 256; i++) {
            lut.put(0, i, (int) (Math.pow((double) i / 255.0, 1 / gamma) * 255.0));
        }
        Core.LUT(src, lut, dest);
    }

    private Mat drawContours() {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat tempSrc = src.clone();
        Mat contoured = new Mat(tempSrc.rows(), tempSrc.cols(), CvType.CV_8UC1);
        Imgproc.findContours(tempSrc, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(contoured, contours, -1, new Scalar(255), 5);
        return contoured;
    }

    private void adaptiveThreshold(Mat source, Mat destination) {
        Imgproc.adaptiveThreshold(source, destination, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.ADAPTIVE_THRESH_MEAN_C, 207, 3);
    }

    private void canny() {
        double mean = getMatMean();
        double sigma = 0.33;
        int lowThreshold = (int) Math.max(0, (1.0 - sigma) * mean);
        int highThreshold = (int) Math.max(255, (1.0 + sigma) * mean);
        Imgproc.Canny(src, dest, lowThreshold, highThreshold);
    }

    private double getMatMean() {
        Scalar meanScalar = Core.mean(src);
        double mean = meanScalar.val[0];
        return mean;
    }

    private Mat correctPerspective(Mat toCorrect) {

        double threshold = 0.05;
        List<MatOfPoint> contours = new ArrayList<>();
        Mat tempSrc = toCorrect.clone();
        Imgproc.findContours(tempSrc, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = -1;
        MatOfPoint temp_contour = contours.get(0); // the largest is at the index 0 for starting point
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourArea = Imgproc.contourArea(temp_contour);
            // compare to previous largest contour
            if (contourArea > maxArea) {
                // check if this contour is a square
                MatOfPoint2f square = new MatOfPoint2f(temp_contour.toArray());
                int contourSize = (int) temp_contour.total();
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                Imgproc.approxPolyDP(square, approxCurve_temp, contourSize * threshold, true);
                if (approxCurve_temp.total() == 4) {
                    maxArea = contourArea;
                    approxCurve = approxCurve_temp;
                }
            }
        }

        double[] temp_double;
        temp_double = approxCurve.get(0, 0);
        Point p1 = new Point(temp_double[0], temp_double[1]);
        temp_double = approxCurve.get(1, 0);
        Point p2 = new Point(temp_double[0], temp_double[1]);
        temp_double = approxCurve.get(2, 0);
        Point p3 = new Point(temp_double[0], temp_double[1]);
        temp_double = approxCurve.get(3, 0);
        Point p4 = new Point(temp_double[0], temp_double[1]);
        List<Point> source = new ArrayList<Point>();
        source.add(p1);
        source.add(p2);
        source.add(p3);
        source.add(p4);
        Mat cornerPoints = Converters.vector_Point2f_to_Mat(source);
        Mat result = warp(toCorrect, cornerPoints);

        return result;
    }

    private Mat warp(Mat inputMat, Mat cornerPoints) {

        int resultWidth = inputMat.width();
        int resultHeight = inputMat.height();

        //point(col, row)
        Point p2 = new Point(0, 0);
        Point p1 = new Point(resultWidth, 0);
        Point p4 = new Point(resultWidth, resultHeight);
        Point p3 = new Point(0, resultHeight);
        List<Point> dest = new ArrayList<Point>();
        dest.add(p1);
        dest.add(p2);
        dest.add(p3);
        dest.add(p4);

        Mat output = new Mat(resultWidth, resultHeight, CvType.CV_8UC1);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(cornerPoints, endM);
        Imgproc.warpPerspective(inputMat, output, perspectiveTransform, new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);

        return output;
    }

    private void logMatDataToConsole(String name, Mat toLog) {
        Log.e("IMG_DETAILS: " + name + " ",
                "dim: " + String.valueOf(toLog.dims())
                        + "; channel: " + String.valueOf(toLog.channels())
                        + "; depth: " + String.valueOf(toLog.depth())
        );
    }

    private Mat getRectOfCheckId(Mat image) {
        //rect x=col, y=row, jobb felso sarokbol szamol
        Point p1 = new Point(image.cols() * Constants.Check_ID_Top_DistFrom_Top, 0);
        Point p2 = new Point(image.cols() * Constants.Check_ID_Bottom_DistFrom_Top, image.rows());
        return getMatOfRect(image, p1, p2);
    }

    private Mat getRectOfAmount(Mat image) {
        Point p1 = new Point(image.cols() * Constants.Check_Amount_Top_DistFrom_Top, image.rows() * Constants.Check_Amount_Right_DistFrom_Right);
        Point p2 = new Point(image.cols() * Constants.Check_Amount_Bottom_DistFrom_Top, image.rows());
        return getMatOfRect(image, p1, p2);
    }

    private Mat getRectOfPaidTo(Mat image) {
        Point p1 = new Point(image.cols() * Constants.Check_PaidTo_Top_DistFrom_Top, 0);
        Point p2 = new Point(image.cols() * Constants.Check_PaidTo_Bottom_DistFrom_Top, image.rows());
        return getMatOfRect(image, p1, p2);
    }

    private Mat getMatOfRect(Mat image, Point p1, Point p2) {
        Rect rect = new Rect(p1, p2);
        Mat result = new Mat(image, rect);
        return result;
    }
}
