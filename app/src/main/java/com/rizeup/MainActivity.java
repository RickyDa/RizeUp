package com.rizeup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rizeup.Queue.QueueActivity;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_CODE = 100;
    private StorageTask mUploadTask;
    private DatabaseReference mDataRef;
    private StorageReference mStorageRef;
    private ProgressBar progressBar;

    private String myPic;
    private User theUser;

    private UserFactory uf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mDataRef = FirebaseDatabase.getInstance().getReference("Users");
        this.mStorageRef = FirebaseStorage.getInstance().getReference("User-Images");
        this.mUploadTask = null;
        isStoragePermissionGranted();
        this.progressBar = findViewById(R.id.progressBar);
        // ######################### FOR TESTING #########################
//        uf = new UserFactory(getApplicationContext());
////        uf.deleteUsers();
//        uf.addUsers();

        this.theUser = new User("Ricky", "Rickyyy44@gmail.com", "");
        // ###############################################################

        findViewById(R.id.btnGetInQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent startQ = new Intent(getApplicationContext(), QueueActivity.class);
//                startActivity(startQ);
                if (mUploadTask != null && mUploadTask.isInProgress())
                    Toast.makeText(getApplicationContext(), "Queueing is in Progress", Toast.LENGTH_LONG).show();
                else
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

            final StorageReference stoRef = mStorageRef.child(uri.getLastPathSegment());
            this.mUploadTask = stoRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
//                            Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_LONG).show();
                            stoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    theUser.setImageUrl(uri.toString());
                                    String uploadId = mDataRef.push().getKey();
                                    mDataRef.child(uploadId).setValue(theUser);
                                    Intent startQ = new Intent(getApplicationContext(), QueueActivity.class);
                                    startActivity(startQ);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Queueing Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });

            Bitmap bit = BitmapFactory.decodeFile(myPic);
            ImageView v = findViewById(R.id.imageView);
            v.setImageBitmap(bit);
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
