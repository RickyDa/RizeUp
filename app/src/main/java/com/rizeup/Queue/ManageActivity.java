package com.rizeup.Queue;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.R;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;

public class ManageActivity extends RiZeUpQueueActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        this.queueName = findViewById(R.id.manage_queueName);
        this.queueImage = findViewById(R.id.manage_QueueImage);

        this.participants = new ArrayList<>();
        this.queue = findViewById(R.id.manage_queueRecyclerView);
        this.loaded = false;
        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        this.participantsRef = queueRef.child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);

//        /// ### TODO : REMOVE AFTER TESTING
//        participantsRef.child("4VvdIHOGo6Rgpx6krnKe8yWO4Nm2").setValue(new QueueParticipant("4VvdIHOGo6Rgpx6krnKe8yWO4Nm2", 15));
//        participantsRef.child("YZZ69efH6afIspSsNWRkO4mPDwc2").setValue(new QueueParticipant("YZZ69efH6afIspSsNWRkO4mPDwc2", 10));
//        participantsRef.child("hEgHL8ja5IXHzreH4ZNdt5P6Sy53").setValue(new QueueParticipant("hEgHL8ja5IXHzreH4ZNdt5P6Sy53", 20));
//        ///     TODO : REMOVE AFTER TESTING ####

        initQueue();
        initItemTouchHelper();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Soon...", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final int i = viewHolder.getAdapterPosition();
                participantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(participants.get(i).getUid())) {
                                snapshot.getRef().removeValue();
                                participants.remove(i);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this.queue);
    }
}
