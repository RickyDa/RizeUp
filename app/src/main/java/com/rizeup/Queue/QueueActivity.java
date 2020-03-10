package com.rizeup.Queue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.CreateQueue.RizeUpQueue;
import com.rizeup.FindQueue.FindQueueActivity;
import com.rizeup.ManageQueue.QueueParticipant;
import com.rizeup.R;
import com.rizeup.SignUp.RiZeUpUser;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class QueueActivity extends AppCompatActivity {

    private DatabaseReference queueRef;
    private DatabaseReference usersRef;

    private RecyclerView queue;
    private ParticipantRecyclerViewAdapter adapter;
    private ArrayList<RiZeUpUser> participants;
    private CircleImageView queueImage;
    private TextView queueName;

    private static final String TAG = "Queue.QueueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        Intent intent = getIntent();
        String message = intent.getStringExtra(FindQueueActivity.QID_EXTRA);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


        this.participants = new ArrayList<>();
        this.queue = findViewById(R.id.userQueue_recyclerView);
        this.queueImage = findViewById(R.id.userQueue_image);
        this.queueName = findViewById(R.id.userQueue_name);

        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES + "/" + message);
        this.usersRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);
        initQueue();

    }

    private void initQueue() {
        this.queue.setHasFixedSize(true);
        this.queue.setLayoutManager(new LinearLayoutManager(this));
        this.queueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RizeUpQueue q = dataSnapshot.getValue(RizeUpQueue.class);
                queueName.setText(q.getName());
                Glide.with(getApplicationContext()).load(q.getImageUrl()).into(queueImage);
                final HashMap<String, QueueParticipant> participantsUid = q.getParticipants();
                if (q.getParticipants() != null) {
                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                RiZeUpUser user = snapshot.getValue(RiZeUpUser.class);
                                if (participantsUid.containsKey(user.getUid()) && !participants.contains(user)) {
                                    participants.add(user);
                                }
                            }
                            adapter = new ParticipantRecyclerViewAdapter(getApplicationContext(), participants);
                            queue.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
