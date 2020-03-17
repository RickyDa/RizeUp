package com.rizeup.CreateQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rizeup.Queue.ManageActivity;
import com.rizeup.RiZeUpActivity;
import com.rizeup.R;
import com.rizeup.models.RiZeUpQueue;
import com.rizeup.utils.FileHandler;
import com.rizeup.utils.FirebaseReferences;

import de.hdodenhof.circleimageview.CircleImageView;


public class CreateQueueActivity extends RiZeUpActivity {

    private FirebaseUser theUser;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private StorageTask mUploadTask;

    private EditText mEditQueueName;
    private CircleImageView mQueueImage;
    private ProgressBar progressBar;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_queue);

        this.theUser = FirebaseAuth.getInstance().getCurrentUser();
        this.databaseRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);
        this.storageRef = FirebaseStorage.getInstance().getReference(FirebaseReferences.STORAGE_QUEUE_IMAGE);

        this.mEditQueueName = findViewById(R.id.enter_q_name);
        this.mQueueImage = findViewById(R.id.queueImg);
        this.progressBar = findViewById(R.id.uploadProgressBar);


        findViewById(R.id.changeImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Creation in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkStoragePermission())
                        openFileChooser();
                    else
                        requestPermission(STORAGE_CODE_REQUEST);
                }
            }
        });

        findViewById(R.id.createQBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(getApplicationContext(), "Creation in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mEditQueueName.getText().toString().trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "***MUST ENTER QUEUE NAME***", Toast.LENGTH_SHORT).show();
                        } else {
                            String qKey = databaseRef.push().getKey();
                            uploadQueueImage(qKey);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Must Select Image For the QUEUE to proceed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getLastLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == STORAGE_CODE_REQUEST && data.getData() != null) {
                this.imageUri = data.getData();
            }
            Glide.with(this).load(this.imageUri).into(this.mQueueImage);
        }
    }

    private void uploadQueueImage(final String qKey) {
        final StorageReference stoRef = storageRef.child(qKey + "." + FileHandler.getFileExtension(getContentResolver(), this.imageUri));
        this.mUploadTask = stoRef.putFile(this.imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        stoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                createQueue(qKey, uri.toString());
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
                        double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        progressBar.setProgress((int) progress);
                    }
                });
    }

    public void createQueue(String qKey, String imageDownLoadUrl) {

        RiZeUpQueue q = new RiZeUpQueue(mEditQueueName.getText().toString(), theUser.getDisplayName(), theUser.getUid(), qKey, imageDownLoadUrl, lat, lng, null);
        DatabaseReference child = databaseRef.child(theUser.getUid());
        child.setValue(q).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_LONG).show();
                        progressBar.setProgress(0);
                    }
                }, 500);
                //start Manage activity
                startActivity(new Intent(getApplicationContext(), ManageActivity.class));
                finish();
                System.currentTimeMillis();
            }
        });

    }

}
