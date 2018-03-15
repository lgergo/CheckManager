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

import java.io.FileOutputStream;
import java.io.IOException;
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

        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        gammaCorrection(src, src);
        Imgproc.equalizeHist(src, src);
        Imgproc.threshold(src, src, getMatMean(src), 256, Imgproc.THRESH_BINARY);

        Mat contoured = drawContours(src);
        Mat cornersOfContour = correctPerspective(contoured);
        corrected = warp(src, cornersOfContour);

        Bitmap output = Bitmap.createBitmap(corrected.cols(), corrected.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(corrected, output);

        //---------------------

        //Mat rect = getRectOfPaidTo(corrected);
//        Core.bitwise_not(rect,rect);
//        Mat erode=Mat.zeros(new Size(3,3),Imgproc.MORPH_RECT);
//        Imgproc.erode(rect,rect,erode);
//        Mat dilate=Mat.ones(new Size(3,3),Imgproc.MORPH_RECT);
//        Imgproc.dilate(rect,rect,dilate);
//        Bitmap output = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(rect, output);

        Log.e("Preprocess", "PREPROCESSING ENDED");
        return output;
    }

    public String[] recognition() {
//
//        String reuslt = tessTwoApi.startRecognition(toTestOnly);
//        return new String[]{reuslt, "-", "-"};

//        //TODO külön szálon fusson
        Mat rect = getRectOfCheckId(corrected);
        adaptiveThreshold(rect, rect);
        Bitmap rectBitmapTemp = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rect, rectBitmapTemp);
        checkIdResult = tessTwoApi.startRecognition(rectBitmapTemp, "0123456789");

        rect = getRectOfAmount(corrected);
        rectBitmapTemp = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rect, rectBitmapTemp);
        amountResult = tessTwoApi.startRecognition(rectBitmapTemp, "0123456789*<>");

        rect = getRectOfPaidTo(corrected);
        rectBitmapTemp = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rect, rectBitmapTemp);
        paidToResult = tessTwoApi.startRecognition(rectBitmapTemp, null);

        return new String[]{checkIdResult, amountResult, paidToResult};
    }

    private void gammaCorrection(Mat source, Mat destination) {
        double gamma = 2.0;
        Mat lut = new Mat(1, 256, CvType.CV_8UC1);
        for (int i = 0; i < 256; i++) {
            lut.put(0, i, (int) (Math.pow((double) i / 255.0, 1 / gamma) * 255.0));
        }
        Core.LUT(source, lut, destination);
    }

    private Mat drawContours(Mat source) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat tempSrc = source.clone();
        Mat contoured = new Mat(tempSrc.rows(), tempSrc.cols(), CvType.CV_8UC1);
        Imgproc.findContours(tempSrc, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(contoured, contours, -1, new Scalar(255), 5);
        return contoured;
    }

    private void adaptiveThreshold(Mat source, Mat destination) {
        Imgproc.adaptiveThreshold(source, destination, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.ADAPTIVE_THRESH_MEAN_C, 207, 3);
    }

    private void canny(Mat source) {
        double mean = getMatMean(source);
        double sigma = 0.33;
        int lowThreshold = (int) Math.max(0, (1.0 - sigma) * mean);
        int highThreshold = (int) Math.max(255, (1.0 + sigma) * mean);
        Imgproc.Canny(source, source, lowThreshold, highThreshold);
    }

    private double getMatMean(Mat meanToGet) {
        Scalar meanScalar = Core.mean(meanToGet);
        return meanScalar.val[0];
    }

    private Mat correctPerspective(Mat toCorrect) {

        double threshold = 0.1;
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

        return Converters.vector_Point2f_to_Mat(source);
    }

    private Mat warp(Mat inputMat, Mat cornerPoints) {

        int resultHeight = inputMat.rows();
        int resultWidth = inputMat.cols();

        Point p1 = new Point(0, 0);
        Point p2 = new Point(resultWidth, 0);
        Point p3 = new Point(resultWidth, resultHeight);
        Point p4 = new Point(0, resultHeight);

        //más telefonokon?
//        if (inputMat.height() > inputMat.width()) {
//            // int temp = resultWidth;
//            // resultWidth = resultHeight;
//            // resultHeight = temp;
//
//            p3 = new Point(0, 0);
//            p4 = new Point(0, resultHeight);
//            p1 = new Point(resultWidth, resultHeight);
//            p2 = new Point(resultWidth, 0);
//        }

        List<Point> dest = new ArrayList<Point>();
        dest.add(p1);
        dest.add(p2);
        dest.add(p3);
        dest.add(p4);

        Mat output = new Mat(resultHeight, resultWidth, CvType.CV_8UC1);
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
        Point p1 = new Point(0, image.rows() * Constants.Check_ID_Top_DistFrom_Top);
        Point p2 = new Point(image.cols(), image.rows() * Constants.Check_ID_Bottom_DistFrom_Top);
        return getMatOfRect(image, p1, p2);
    }

    private Mat getRectOfAmount(Mat image) {
        Point p1 = new Point(image.cols() * Constants.Check_Amount_Right_DistFrom_Right, image.rows() * Constants.Check_Amount_Top_DistFrom_Top);
        Point p2 = new Point(image.cols(), image.rows() * Constants.Check_Amount_Bottom_DistFrom_Top);
        return getMatOfRect(image, p1, p2);
    }

    private Mat getRectOfPaidTo(Mat image) {
        Point p1 = new Point(0, image.rows() * Constants.Check_PaidTo_Top_DistFrom_Top);
        Point p2 = new Point(image.cols(), image.rows() * Constants.Check_PaidTo_Bottom_DistFrom_Top);
        return getMatOfRect(image, p1, p2);
    }

    private Mat getMatOfRect(Mat image, Point p1, Point p2) {
        Rect rect = new Rect(p1, p2);
        Mat result = new Mat(image, rect);
        return result;
    }

    private void writeImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/storage/sdcard/Android/data/com.yevsp8.checkmanager/files/Pictures/edited_d.png");
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
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
}
