package com.rizeup.FindQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rizeup.CreateQueue.RizeUpQueue;
import com.rizeup.Queue.QueueActivity;
import com.rizeup.R;
import com.rizeup.utils.FirebaseReferences;
import com.rizeup.utils.User;
import com.rizeup.utils.UserFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FindQueueActivity extends AppCompatActivity {
    private static final String TAG = "FindQueueActivity";
    private static final int REQUEST_CAMERA_CODE = 100;

    private RecyclerView queueListRecyclerView;
    private QueueListRecyclerViewAdapter qListAdapter;
    private DatabaseReference databaseQueueRef;
    private ArrayList<RizeUpQueue> qList;

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
        setContentView(R.layout.activity_find_queue);
        this.qList = new ArrayList<>();
        this.queueListRecyclerView = findViewById(R.id.recyclerViewQList);
        this.queueListRecyclerView.setHasFixedSize(true);
        this.queueListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.databaseQueueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);

        databaseQueueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RizeUpQueue q = snapshot.getValue(RizeUpQueue.class);
                    qList.add(q);
                }
                qListAdapter = new QueueListRecyclerViewAdapter(getApplicationContext(), qList);
                queueListRecyclerView.setAdapter(qListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });







        // TODO: need to edit this.
//        this.mDataRef = FirebaseDatabase.getInstance().getReference("Users");
//        this.mStorageRef = FirebaseStorage.getInstance().getReference("User-Images");
//        this.mUploadTask = null;
//
//        this.progressBar = findViewById(R.id.progressBar);
//        // ######################### FOR TESTING #########################
////        uf = new UserFactory(getApplicationContext());
//////        uf.deleteUsers();
////        uf.addUsers();
//
//        this.theUser = new User("Ricky", "Rickyyy44@gmail.com", "");
//        // ###############################################################
//
//        findViewById(R.id.btnGetInQ).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent startQ = new Intent(getApplicationContext(), QueueActivity.class);
////                startActivity(startQ);
//                if (mUploadTask != null && mUploadTask.isInProgress())
//                    Toast.makeText(getApplicationContext(), "Queueing is in Progress", Toast.LENGTH_LONG).show();
//                else
//                    dispatchTakePictureIntent();
//            }
//        });
////
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

//            Bitmap bit = BitmapFactory.decodeFile(myPic);
//            ImageView v = findViewById(R.id.imageView);
//            v.setImageBitmap(bit);
        }
    }
}
