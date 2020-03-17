package com.rizeup.Queue;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.rizeup.FindQueue.FindQueueActivity;
import com.rizeup.FindQueue.MapDialog;
import com.rizeup.R;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;

public class QueueActivity extends RiZeUpQueueActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        Intent intent = getIntent();
        String queueId = intent.getStringExtra(FindQueueActivity.QID_EXTRA);


        this.loaded = false;
        this.participants = new ArrayList<>();
        this.queueRecyclerView = findViewById(R.id.userQueue_recyclerView);
        this.queueImageView = findViewById(R.id.userQueue_image);
        this.queueNameTextView = findViewById(R.id.userQueue_name);

        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES + "/" + queueId);
        this.participantsRef = queueRef.child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);
        FloatingActionButton fab = findViewById(R.id.userQueue_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        initQueue();

    }

    private void openDialog() {
        MapDialog md = new MapDialog(queueLat, queueLng, queueName, queueImageUrl);
        md.show(getSupportFragmentManager(), "QUEUE");
    }



}
