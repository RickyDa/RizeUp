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
    public static final String QID_EXTRA = "qid";
    private static final String TAG = "FindQueueActivity";

    private RecyclerView queueListRecyclerView;
    private QueueListRecyclerViewAdapter qListAdapter;
    private DatabaseReference databaseQueueRef;
    private ArrayList<RizeUpQueue> qList;


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
    }


}
