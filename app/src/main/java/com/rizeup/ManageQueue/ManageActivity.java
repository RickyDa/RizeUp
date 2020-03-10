package com.rizeup.ManageQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.CreateQueue.RizeUpQueue;
import com.rizeup.Queue.RecyclerViewAdapter;
import com.rizeup.R;
import com.rizeup.SignUp.RiZeUpUser;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageActivity extends AppCompatActivity {

    private FirebaseUser theUser;
    private DatabaseReference queueRef;
    private DatabaseReference usersRef;
    private DatabaseReference participantsRef;

    private TextView name;
    private CircleImageView image;
    private RecyclerView queue;
    private RecyclerViewAdapter adapter;

    private ArrayList<RiZeUpUser> participants;

    public ManageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        this.name = findViewById(R.id.manage_queueName);
        this.image = findViewById(R.id.manage_QueueImage);

        this.participants = new ArrayList<>();
        this.queue = findViewById(R.id.manage_queueRecyclerView);

        this.theUser = FirebaseAuth.getInstance().getCurrentUser();
        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES).child(theUser.getUid());
        this.participantsRef = queueRef.child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);
        this.usersRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);

        /// ### TODO : REMOVE AFTER TESTING
        participantsRef.child("4VvdIHOGo6Rgpx6krnKe8yWO4Nm2").setValue(new QueueParticipant("4VvdIHOGo6Rgpx6krnKe8yWO4Nm2", System.currentTimeMillis()));
        participantsRef.child("YZZ69efH6afIspSsNWRkO4mPDwc2").setValue(new QueueParticipant("YZZ69efH6afIspSsNWRkO4mPDwc2", System.currentTimeMillis()));
        participantsRef.child("hEgHL8ja5IXHzreH4ZNdt5P6Sy53").setValue(new QueueParticipant("hEgHL8ja5IXHzreH4ZNdt5P6Sy53", System.currentTimeMillis()));
        ///     TODO : REMOVE AFTER TESTING ####

        initQueue();
        initItemTouchHelper();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Soon...", Toast.LENGTH_LONG).show();
                participantsRef.child("IQqVjN5pPHT4iGlze3Qc8KIGdFe2").setValue(new QueueParticipant("IQqVjN5pPHT4iGlze3Qc8KIGdFe2", System.currentTimeMillis()));
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

    private void initQueue() {
        this.queue.setHasFixedSize(true);
        this.queue.setLayoutManager(new LinearLayoutManager(this));
        this.queueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RizeUpQueue q = dataSnapshot.getValue(RizeUpQueue.class);
                name.setText(q.getName());
                Glide.with(getApplicationContext()).load(q.getImageUrl()).into(image);
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
                            adapter = new RecyclerViewAdapter(getApplicationContext(), participants);
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
