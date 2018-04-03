package com.yevsp8.checkmanager.view;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yevsp8.checkmanager.ImageProcessor;
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerImageProcessingComponent;
import com.yevsp8.checkmanager.di.ImageProcessingComponent;
import com.yevsp8.checkmanager.di.TessTwoModule;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class NewImageActivity extends BaseActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    @Inject
    ImageProcessor processor;

    private Button buttonRecognise;
    private ImageView imageView;
    private Bitmap myBitmap;
    private String currentPhotoPath;
    private ProgressDialog progress;
    private String[] recognisedTexts;

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
        buttonRecognise.setEnabled(false);
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
                // TODO Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                myBitmap = processor.rotate(myBitmap, currentPhotoPath);
                imageView.setImageBitmap(myBitmap);
            }
            FragmentManager manager = getSupportFragmentManager();
            removeFragmentFromActivtiy(manager, fragment);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

    public String[] trimRecogniseResults(String[] results) {
        String id = results[0].replaceAll(" ", "");
        int amountValue;
        String trimmed = results[1].replaceAll("(\\*| )", "");
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

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(NewImageActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            startRecognition();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecognition();
                } else {
                    buttonRecognise.setEnabled(false);
                }
                return;
            }
        }
    }

    private void startPreprocessing() {


        //demohoz
        currentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/normal.jpg";

        new ImagePreprocessingTask().execute(currentPhotoPath);


//            Log.e("MEM", err.getMessage());
//            //TODO message hogy próbálja újra
//            Intent settings = new Intent(getApplicationContext(), NewImageActivity.class);
//            startActivity(settings);

    }

    private void startRecognition() {


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Megfelelő a kép?");
//        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent settings = new Intent(getApplicationContext(), NewImageActivity.class);
//                startActivity(settings);
//            }
//        });
//        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//        String[] recognisedText = processor.recognition();

        try {
            new TextRecognitionTask().execute();
        } catch (Exception ex) {
            Log.e("rec", ex.getMessage());
        }


//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
//        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.show();
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
        }

        @Override
        protected void onPostExecute(Bitmap output) {
            FragmentManager manager = getSupportFragmentManager();
            removeFragmentFromActivtiy(manager, fragment);
            imageView.setImageBitmap(output);
            buttonRecognise.setEnabled(true);
            progress.dismiss();
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
        }
    }

    private class TextRecognitionTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            try {
                return startRecognition();
            } catch (Exception e) {
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
        }

        @Override
        protected void onPostExecute(String[] output) {
            recognisedTexts = output;
            recognisedTexts = trimRecogniseResults(recognisedTexts);
            progress.dismiss();
            Intent intent = new Intent(getApplicationContext(), CheckDetailsActivity.class);
            intent.putExtra("result_array", recognisedTexts);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
        }
    }
}
