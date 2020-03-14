package com.rizeup.Queue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.rizeup.FindQueue.FindQueueActivity;
import com.rizeup.R;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;

public class QueueActivity extends RiZeUpQueueActivity {


    private static final String TAG = "Queue.QueueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        Intent intent = getIntent();
        String queueId = intent.getStringExtra(FindQueueActivity.QID_EXTRA);
        Toast.makeText(getApplicationContext(), queueId, Toast.LENGTH_SHORT).show();

        this.loaded = false;
        this.participants = new ArrayList<>();
        this.queue = findViewById(R.id.userQueue_recyclerView);
        this.queueImage = findViewById(R.id.userQueue_image);
        this.queueName = findViewById(R.id.userQueue_name);

        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES + "/" + queueId);
        this.participantsRef = queueRef.child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);
        initQueue();

    }

}
