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
    protected RecyclerView queue;
    protected ParticipantRecyclerViewAdapter adapter;
    protected ArrayList<QueueParticipant> participants;
    protected CircleImageView queueImage;
    protected TextView queueName;


    protected void initQueue() {
        this.queue.setHasFixedSize(true);
        this.queue.setLayoutManager(new LinearLayoutManager(this));
        this.queueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RiZeUpQueue q = dataSnapshot.getValue(RiZeUpQueue.class);

                queueName.setText(q.getName());
                Glide.with(getApplicationContext()).load(q.getImageUrl()).into(queueImage);

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
                                queue.setAdapter(adapter);
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
