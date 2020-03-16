package com.rizeup.Queue;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.models.QueueParticipant;
import com.rizeup.models.RiZeUpQueue;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiZeUpQueueActivity extends AppCompatActivity {

    protected DatabaseReference queueRef;
    protected DatabaseReference participantsRef;
    protected boolean loaded;
    protected RecyclerView queueRecyclerView;
    protected ParticipantRecyclerViewAdapter adapter;
    protected ArrayList<QueueParticipant> participants;
    protected CircleImageView queueImageView;
    protected TextView queueNameTextView;
    protected double queueLat;
    protected double queueLng;
    protected String queueImageUrl;
    protected String queueName;

    protected void initQueue() {
        this.queueRecyclerView.setHasFixedSize(true);
        this.queueRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.queueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RiZeUpQueue q = dataSnapshot.getValue(RiZeUpQueue.class);
                assert q != null;
                queueLat = q.getLat();
                queueLng = q.getLng();
                queueName = q.getName();
                queueImageUrl = q.getImageUrl();

                queueNameTextView.setText(queueName);
                Glide.with(getApplicationContext()).load(queueImageUrl).into(queueImageView);

                if (q.getParticipants() != null) {
                    participantsRef.orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                QueueParticipant qp = s.getValue(QueueParticipant.class);
                                if (!participants.contains(qp)) {
                                    participants.add(qp);
                                }
                            }
                            if (!loaded) {
                                adapter = new ParticipantRecyclerViewAdapter(getApplicationContext(), participants);
                                queueRecyclerView.setAdapter(adapter);
                                loaded = true;
                            } else {
                                adapter.notifyDataSetChanged();
                            }
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
