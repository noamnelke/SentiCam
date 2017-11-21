package com.example.noamnelke.senticam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
        implements SelectPhotoFragment.OnFragmentInteractionListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_PHOTO = 2;

    private String mCurrentPhotoPath;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private SelectPhotoFragment selectPhotoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;

            }

            selectPhotoFragment = new SelectPhotoFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, selectPhotoFragment).commit();
        }
    }

    @Override
    public void onSnapPhotoPressed() {
        dispatchTakePictureIntent();
    }

    @Override
    public void onSelectFromGalleryPressed() {
        dispatchSelectFromGalleryIntent();
    }

    private void dispatchSelectFromGalleryIntent() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, REQUEST_PICK_PHOTO);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "FACE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                logger.warning("Error occurred while creating the File");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectPhotoFragment.setResult("Detecting sentiment...", null);
            Bitmap image = null;
            if (requestCode == REQUEST_PICK_PHOTO) {
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == REQUEST_TAKE_PHOTO) {
                image = BitmapFactory.decodeFile(mCurrentPhotoPath);
            }
            SentimentDetectionClient client = SentimentDetectionClient.getInstance(this);
            client.recognize(
                    image,
                    new SentimentDetectionClient.DetectionResultsHandler() {
                        @Override
                        public void handleResults(String res, Bitmap image) {
                            selectPhotoFragment.setResult(res, image);
                        }
                    }
            );
        }
    }
}
