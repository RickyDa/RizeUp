package com.rizeup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.rizeup.utils.FileHandler;

import java.io.File;
import java.io.IOException;

@SuppressLint("Registered")
public class RiZeUpActivity extends AppCompatActivity {

    protected final int CAMERA_CODE_REQUEST = 100;
    protected final int STORAGE_CODE_REQUEST = 101;
    protected Uri imageUri;

    public void requestPermission(int requestCode) {
        switch (requestCode) {
            case CAMERA_CODE_REQUEST:
                requestCameraPermission(requestCode);
                break;
            case STORAGE_CODE_REQUEST:
                requestStoragePermission(requestCode);
        }
    }

    private void requestStoragePermission(int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, requestCode);
    }

    private void requestCameraPermission(int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, requestCode);
    }


    protected boolean checkCameraPermission() {
        return checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    protected boolean checkStoragePermission() {
        return checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    protected void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, STORAGE_CODE_REQUEST);
    }

    protected void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = FileHandler.createImageFile(this);
            } catch (IOException ex) {
                Log.d("CAMERA", "openCamera() " + ex.getMessage());
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.rizeup.fileprovider",
                        photoFile);
                this.imageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_CODE_REQUEST);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean flag = grantResults.length > 0;
        if (flag) {
            for (int res : grantResults) {
                flag = flag && (res == PackageManager.PERMISSION_GRANTED);
            }
        }
        if (flag) {
            if (requestCode == CAMERA_CODE_REQUEST) {
                openCamera();
            } else if (requestCode == STORAGE_CODE_REQUEST) {
                openFileChooser();
            }
        }
    }

}
