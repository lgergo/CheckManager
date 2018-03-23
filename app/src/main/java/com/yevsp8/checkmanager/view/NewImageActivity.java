package com.yevsp8.checkmanager.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yevsp8.checkmanager.HelpFragment;
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
    private Button buttonTakePhoto;
    private Button buttonRecognise;
    private Button buttonDemo;
    private ImageView imageView;
    private Bitmap myBitmap;
    private String currentPhotoPath;

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

        buttonTakePhoto = findViewById(R.id.button_capture_photo);
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

        buttonDemo = findViewById(R.id.button_demoData);
        buttonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreprocessing();
            }
        });
        loadDemoImage();
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
                //options.inSampleSize = 2;
                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //        myBitmap = processor.rotate(myBitmap,currentPhotoPath);
                imageView.setImageBitmap(myBitmap);
            }
            buttonRecognise.setEnabled(true);
            FragmentManager manager = getSupportFragmentManager();
            removeFragmentFromActivtiy(manager, fragment);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
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

    //TODO ha nem jó a kép akkor ne is mentse le / vagy töröljük


    private void startRecognition() {
        String[] recognisedText = processor.recognition();
        Intent intent = new Intent(this, CheckDetailsActivity.class);
        intent.putExtra("result_array", recognisedText);
        startActivity(intent);
    }

    private void loadDemoImage() {
        String imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/skew.jpg";
        currentPhotoPath = imagePath;

        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 2;
        myBitmap = BitmapFactory.decodeFile(imagePath, options);
        myBitmap = processor.rotate(myBitmap, currentPhotoPath);
        imageView = findViewById(R.id.captured_photo_imageView);
        imageView.setImageBitmap(myBitmap);

        buttonRecognise.setEnabled(true);

        FragmentManager manager = getSupportFragmentManager();
        removeFragmentFromActivtiy(manager, fragment);
    }

    private void startPreprocessing() {
        Bitmap b = processor.preProcessing(myBitmap, currentPhotoPath);
        imageView.setImageBitmap(b);
    }
}
