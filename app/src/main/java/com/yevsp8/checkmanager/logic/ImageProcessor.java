package com.yevsp8.checkmanager.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 03. 04..
 */

public class ImageProcessor {

    @Inject
    TessTwoApi tessTwoApi;
    private Mat corrected;
    private Bitmap sourceBitmap;

    public ImageProcessor(Context context) {
        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(context))
                .tessTwoModule(new TessTwoModule(context))
                .build();
        component.injectImageProcessor(this);

        try {
            OpenCVLoader.initDebug();
        } catch (Exception ex) {
            Log.e("OpenCV", ex.getMessage());
        }
    }

    private void loadImageFromFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 2;
        sourceBitmap = BitmapFactory.decodeFile(path, options);
        sourceBitmap = rotate(sourceBitmap, path);
    }

    public Bitmap preProcessing(String filePath) {

        Log.e("Tess", "Ppeprocess started");
        loadImageFromFile(filePath);
        Mat src = new Mat(sourceBitmap.getHeight(), sourceBitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(sourceBitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Mat checkRect = src.clone();
        corrected = src.clone();

        /*
        a)
            Imgproc.medianBlur(checkRect, checkRect, 7);
            Imgproc.equalizeHist(checkRect, checkRect);
            Imgproc.Canny(checkRect,checkRect,getMatMean(checkRect)*0.2,getMatMean(checkRect)*1.8);

        b)
            Imgproc.medianBlur(checkRect, checkRect, 13);
            Imgproc.adaptiveThreshold(checkRect,checkRect,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,13,3);
            Core.bitwise_not(checkRect,checkRect);
            Mat kernel = Mat.ones(7, 7, CvType.CV_8UC1);
            Imgproc.morphologyEx(checkRect,checkRect,Imgproc.MORPH_OPEN,kernel);

        c)
            Imgproc.medianBlur(checkRect, checkRect, 7);
            Imgproc.equalizeHist(checkRect, checkRect);
            Imgproc.threshold(checkRect, checkRect, getMatMean(checkRect) * 1.1, 256, Imgproc.THRESH_BINARY);
        */

        Imgproc.medianBlur(checkRect, checkRect, 13);
        Imgproc.adaptiveThreshold(checkRect, checkRect, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 13, 3);
        Core.bitwise_not(checkRect, checkRect);

        Mat kernel = Mat.ones(11, 11, CvType.CV_8UC1);
        Imgproc.morphologyEx(checkRect, checkRect, Imgproc.MORPH_GRADIENT, kernel);

        List<MatOfPoint> contours = new ArrayList<>();
        findAndSetContoursList(checkRect, contours);
        //Mat contoured = drawContours(checkRect, contours);
        Mat cornersOfContour = getCorrectedPerspectiveRectPoints(contours);
        corrected = warp(src, cornersOfContour);

        //corrected=checkRect;
        //corrected=contoured;

//        Mat                rect = getRectOfPaidTo(corrected);
//        preprocessForRectImages(rect, 4, 1);
//        Bitmap rectBitmapTemp = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_4444);
//        Utils.matToBitmap(rect, rectBitmapTemp);
//        return  rectBitmapTemp;


        Utils.matToBitmap(corrected, sourceBitmap);
        return sourceBitmap;
    }

    public String[] recognition() throws IOException, RuntimeException {

        Bitmap rectBitmapTemp;
        tessTwoApi.initialize();

        Mat rect = getRectOfAmount(corrected);
        preprocessForRectImages(rect, 1, 2);
        rectBitmapTemp = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(rect, rectBitmapTemp);
        String amountResult = tessTwoApi.startRecognition(rectBitmapTemp, Constants.WhiteListAmount);

        rect = getRectOfPaidTo(corrected);
        preprocessForRectImages(rect, 4, 1);
        rectBitmapTemp = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(rect, rectBitmapTemp);
        String paidToResultLines = tessTwoApi.startRecognition(rectBitmapTemp, null);
        String[] resultLines = paidToResultLines.split("\n");
        String paidToResult = resultLines[1];

        rect = getRectOfCheckId(corrected);
        preprocessForRectImages(rect, 1, 3);
        rectBitmapTemp = Bitmap.createBitmap(rect.cols(), rect.rows(), Bitmap.Config.ARGB_4444);
        Utils.matToBitmap(rect, rectBitmapTemp);
        String checkIdResult = tessTwoApi.startRecognition(rectBitmapTemp, Constants.WhiteListId);

        return new String[]{checkIdResult, amountResult, paidToResult};
    }

    private void preprocessForRectImages(Mat rect, int erodeSize, int dilateSize) {
        Imgproc.threshold(rect, rect, 0, 255, Imgproc.THRESH_OTSU);
        Core.bitwise_not(rect, rect);
        if (erodeSize != 0) {
            Mat erode = Mat.ones(new Size(erodeSize, erodeSize), Imgproc.MORPH_RECT);
            Imgproc.erode(rect, rect, erode);
        }
        if (dilateSize != 0) {
            Mat dilate = Mat.ones(new Size(dilateSize, dilateSize), Imgproc.MORPH_RECT);
            Imgproc.dilate(rect, rect, dilate);
        }
    }

//    private void gammaCorrection(Mat source, Mat destination) {
//        double gamma = 2.0;
//        Mat lut = new Mat(1, 256, CvType.CV_8UC1);
//        for (int i = 0; i < 256; i++) {
//            lut.put(0, i, (int) (Math.pow((double) i / 255.0, 1 / gamma) * 255.0));
//        }
//        Core.LUT(source, lut, destination);
//    }
//
private double getMatMean(Mat meanToGet) {
    Scalar meanScalar = Core.mean(meanToGet);
    return meanScalar.val[0];
}

    private void findAndSetContoursList(Mat source, List<MatOfPoint> contours) {
        Imgproc.findContours(source, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
    }

//    private Mat drawContours(Mat source, List<MatOfPoint> contours) {
//        Mat tempSrc = source.clone();
//        Mat contoured = new Mat(tempSrc.rows(), tempSrc.cols(), CvType.CV_8UC1);
//        Imgproc.drawContours(contoured, contours, -1, new Scalar(255), 5);
//        return contoured;
//    }

    private Mat getCorrectedPerspectiveRectPoints(List<MatOfPoint> contours) {
        double threshold = 0.5;
        double maxArea = 1000;
        MatOfPoint temp_contour;
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourArea = Imgproc.contourArea(temp_contour);
            if (contourArea > maxArea) {
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
        List<Point> source = new ArrayList<>();
        source.add(p1);
        source.add(p2);
        source.add(p3);
        source.add(p4);
        reorderRectClockwise(source);

        return Converters.vector_Point2f_to_Mat(source);
    }

    private void reorderRectClockwise(List<Point> points) {
        Collections.sort(points, new Comparator<Point>() {

            public int compare(Point p1, Point p2) {
                return Double.compare(p1.x + p1.y, p2.x + p2.y);
            }
        });
        Collections.swap(points, 2, 3);
    }

    private Mat warp(Mat inputMat, Mat cornerPoints) {

        int resultHeight = inputMat.rows();
        int resultWidth = inputMat.cols();

        Point p1 = new Point(0, 0);
        Point p2 = new Point(resultWidth, 0);
        Point p3 = new Point(resultWidth, resultHeight);
        Point p4 = new Point(0, resultHeight);

        List<Point> dest = new ArrayList<>();
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

    private Mat getRectOfCheckId(Mat image) {
        Point p1 = new Point(image.cols() * Constants.Check_ID_Left_DistFrom_Left, image.rows() * Constants.Check_ID_Top_DistFrom_Top);
        Point p2 = new Point(image.cols() * Constants.Check_ID_Right_DistFrom_Left, image.rows() * Constants.Check_ID_Bottom_DistFrom_Top);
        return getMatOfRect(image, p1, p2);
    }

    private Mat getRectOfAmount(Mat image) {
        Point p1 = new Point(image.cols() * Constants.Check_Amount_Left_DistFrom_Left, image.rows() * Constants.Check_Amount_Top_DistFrom_Top);
        Point p2 = new Point(image.cols() * Constants.Check_Amount_Right_DistFrom_Left, image.rows() * Constants.Check_Amount_Bottom_DistFrom_Top);
        return getMatOfRect(image, p1, p2);
    }

    private Mat getRectOfPaidTo(Mat image) {
        Point p1 = new Point(image.cols() * Constants.Check_PaidTo_Left_DistFrom_Left, image.rows() * Constants.Check_PaidTo_Top_DistFrom_Top);
        Point p2 = new Point(image.cols() * Constants.Check_PaidTo_Right_DistFrom_Left, image.rows() * Constants.Check_PaidTo_Bottom_DistFrom_Top);
        return getMatOfRect(image, p1, p2);
    }

    private Mat getMatOfRect(Mat image, Point p1, Point p2) {
        Rect rect = new Rect(p1, p2);
        return new Mat(image, rect);
    }

//    private void writeImage(Bitmap bmp) {
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream("/storage/sdcard/Android/data/com.yevsp8.checkmanager/files/Pictures/corrected.png");
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public Bitmap rotate(Bitmap source, String currentPhotoPath) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException ex) {
            return source;
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return source;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return source;
        }
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
