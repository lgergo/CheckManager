package com.yevsp8.checkmanager.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;
import com.yevsp8.checkmanager.di.TessTwoModule;
import com.yevsp8.checkmanager.logic.ImageProcessor;
import com.yevsp8.checkmanager.util.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class NewImageActivity extends BaseActivity {

    @Inject
    ImageProcessor processor;

    private Button buttonRecognise;
    private ImageView imageView;
    private String currentPhotoPath;
    private ProgressDialog progress;

    private HelpFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageProcessingComponent component = DaggerImageProcessingComponent.builder()
                .contextModule(new ContextModule(this))
                .tessTwoModule(new TessTwoModule(this))
                .build();
        component.injectNewImageActivtiy(this);

        FragmentManager manager = getSupportFragmentManager();
        fragment = new HelpFragment();
        replaceFragmentToActivity(manager, fragment, R.id.helpText_framgentContainer);

        imageView = findViewById(R.id.captured_photo_imageView);

        Button buttonTakePhoto = findViewById(R.id.button_capture_photo);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        buttonRecognise = findViewById(R.id.button_recognise_photo);
        buttonRecognise.setVisibility(View.GONE);
        buttonRecognise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                permissionCheck();
            }
        });

        Button buttonDemo = findViewById(R.id.button_demoData);
        buttonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreprocessing();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                try {
                    startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_PHOTO);
                } catch (Exception ex) {
                    Log.e("CAM", ex.getMessage());
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myBitmap = processor.rotate(myBitmap, currentPhotoPath);
                imageView.setImageBitmap(myBitmap);
            }
            FragmentManager manager = getSupportFragmentManager();
            removeFragmentFromActivity(manager, fragment);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(Constants.LongDateTimePattern, Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String[] trimRecogniseResults(String[] results) {
        String id = results[0].replaceAll(" ", "");
        int amountValue;
        String trimmed = results[1].replaceAll("\\*|", "");
        try {
            amountValue = Integer.parseInt(trimmed);
        } catch (Exception ex) {
            amountValue = -1;
        }
        return new String[]{id, String.valueOf(amountValue), results[2]};
    }

    private void permissionCheck() {
        if (ContextCompat.checkSelfPermission(NewImageActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewImageActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(NewImageActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            startRecognition();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecognition();
                } else {
                    buttonRecognise.setVisibility(View.GONE);
                }
            }
        }
    }

    private void startPreprocessing() {
        //currentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/test.jpg";
        new ImagePreprocessingTask().execute(currentPhotoPath);
    }

    private void startRecognition() {
        try {
            new TextRecognitionTask().execute();
        } catch (Exception ex) {
            Log.e("rec", ex.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }

    private class ImagePreprocessingTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                return startPreprocessing(strings[0]);
            } catch (Exception e) {
                cancel(true);
                return null;
            }
        }

        private Bitmap startPreprocessing(String path) throws Exception {
            return processor.preProcessing(path);
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(NewImageActivity.this);
            progress.setMessage(getString(R.string.imageProc_processing_processDialog));
            progress.show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(Bitmap output) {
            FragmentManager manager = getSupportFragmentManager();
            removeFragmentFromActivity(manager, fragment);
            imageView.setImageBitmap(output);
            buttonRecognise.setVisibility(View.VISIBLE);
            progress.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private class TextRecognitionTask extends AsyncTask<Void, Void, String[]> {
        Exception lastError;
        @Override
        protected String[] doInBackground(Void... voids) {
            try {
                return startRecognition();
            } catch (Exception e) {
                lastError = e;
                cancel(true);
                return null;
            }
        }

        private String[] startRecognition() throws Exception {
            return processor.recognition();
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(NewImageActivity.this);
            progress.setMessage(getString(R.string.tesseract_recognition_processDialog));
            progress.show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(String[] output) {
            String[] recognisedTexts = output;
            if (recognisedTexts != null) {
                recognisedTexts = trimRecogniseResults(recognisedTexts);
            }
            progress.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Intent intent = new Intent(getApplicationContext(), CheckDetailsActivity.class);
            intent.putExtra(Constants.RecognisedTextsArray, recognisedTexts);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
            String message = getResources().getString(R.string.newImage_tessError_universal);
            if (lastError instanceof RuntimeException) {
                message = getResources().getString(R.string.newImage_tessError_tooLowQuality);
            } else if (lastError instanceof IOException) {
                message = getResources().getString(R.string.newImage_tessError_traineddatzaNotFound);
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(NewImageActivity.this);
            builder.setTitle(R.string.newImage_tessError_alertDialog_title);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                }
            });
            builder.show();
        }
    }
}
