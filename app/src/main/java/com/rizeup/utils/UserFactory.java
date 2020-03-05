package com.rizeup.utils;


import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UserFactory {
    private static final String TAG = "UserFactory";
    private String[] images = new String[]{
            "/user_one.jpg",
            "/user_two.jpg",
            "/user_three.jpg",
            "/user_four.jpg",
            "/user_five.jpg",
            "/user_six.jpg"};

    private String[] names = new String[]{
            "dummy_one",
            "dummy_two",
            "dummy_three",
            "dummy_four",
            "dummy_five",
            "dummy_six"};
    private StorageTask mUploadTask;
    private DatabaseReference mDataRef;
    private StorageReference mStorageRef;
    private Context mContext;

    public UserFactory(Context context) {
        this.mDataRef = FirebaseDatabase.getInstance().getReference("Users");
        this.mStorageRef = FirebaseStorage.getInstance().getReference("User-Images");
        this.mContext = context;
        this.mUploadTask = null;
    }

    public void addUsers() {
        for (int i = 0; i < images.length; i++) {
            Uri image = Uri.fromFile(new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            + images[i]));
            final StorageReference stoRef = mStorageRef.child(images[i]);
            final int currIndex = i;
            this.mUploadTask = stoRef.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            stoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    User user = new User(names[currIndex], names[currIndex] + "@gmail.com",url);

                                    String uploadId = mDataRef.push().getKey();
                                    mDataRef.child(uploadId).setValue(user);
                                }
                            });
                            if (currIndex == images.length - 1)
                                Toast.makeText(mContext, "Upload completed", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Upload Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            while (mUploadTask.isInProgress());
        }
    }

    public void deleteUsers() {
//        mStorageRef.delete();
        mDataRef.removeValue();
    }
}
