package com.rizeup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rizeup.Queue.QueueActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

//TODO:
//  -FLOW (of this function)
//      - after image capture upload the current user
//        to firebase and add the user to the Q
//      - when photo the is in uploading state show progress bar.
//      - when upload is completed start Queue.QueueActivity

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final String IMAGE_DATA = "data";
    private StorageTask mUploadTask;
    private DatabaseReference mDataRef;
    private StorageReference mStorageRef;

    private UserFactory uf;
    private String myPic;

    private User theUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mDataRef = FirebaseDatabase.getInstance().getReference("Users");
        this.mStorageRef = FirebaseStorage.getInstance().getReference("User-Images");
        this.mUploadTask = null;
        boolean flag = isStoragePermissionGranted();

        // ######################### FOR TESTING #########################
//        uf = new UserFactory(getApplicationContext());
//        uf.deleteUsers();
//        uf.addUsers();

        this.theUser = new User("Ricky", "Rickyyy44@gmail.com", "");
        // ###############################################################

        findViewById(R.id.btnGetInQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "MyPic";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        myPic = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "dispatchTakePictureIntent: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.rizeup.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(new File(myPic));

            StorageReference stoRef = mStorageRef.child(uri.getLastPathSegment());
            this.mUploadTask = stoRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                            theUser.setImageUrl(taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = mDataRef.push().getKey();
                            mDataRef.child(uploadId).setValue(theUser);
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            Bitmap bit = BitmapFactory.decodeFile(myPic);
            ImageView v = findViewById(R.id.imageView);
            v.setImageBitmap(bit);
            // TODO: DELAY Progress...TO FIX
            while(mUploadTask.isInProgress());

            Intent startQ = new Intent(getApplicationContext(), QueueActivity.class);
//            Bitmap image = null;
//
//            if (data != null)
//                image = (Bitmap) Objects.requireNonNull(data.getExtras()).get(IMAGE_DATA);

//            startQ.putExtra(IMAGE_DATA, image);
            startActivity(startQ);
        }


    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission is granted");
                return true;
            } else {
                Log.d(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, 1);
                return false;
            }
        } else {
            Log.d(TAG, "Permission is granted");
            return true;
        }
    }

}
