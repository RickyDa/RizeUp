package com.rizeup.ManageQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.CreateQueue.RizeUpQueue;
import com.rizeup.Queue.RecyclerViewAdapter;
import com.rizeup.R;
import com.rizeup.SignUp.RiZeUpUser;
import com.rizeup.utils.FirebaseReferences;
import com.rizeup.utils.User;

import java.util.ArrayList;

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
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;

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
        this.queue.setHasFixedSize(true);
        this.queue.setLayoutManager(new LinearLayoutManager(this));

        this.theUser = FirebaseAuth.getInstance().getCurrentUser();
        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES).child(theUser.getUid());
        this.participantsRef = queueRef.child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);
        this.usersRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);

        initQueue();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Soon...", Toast.LENGTH_LONG).show();
            }
        });

        itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                        participantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.getValue().equals(participants.get(viewHolder.getAdapterPosition()).getUid())){
                                        snapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }


                        });
                        //TODO:
                        //  -try to remove them.
                        participants.remove(viewHolder.getAdapterPosition());
                        adapter.notifyDataSetChanged();
                    }
                };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(queue);


    }

    private void initQueue() {
        this.queueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RizeUpQueue q = dataSnapshot.getValue(RizeUpQueue.class);
                name.setText(q.getName());
                Glide.with(getApplicationContext()).load(q.getImageUrl()).into(image);

                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RiZeUpUser user = snapshot.getValue(RiZeUpUser.class);
                            participants.add(user);
                        }
                        adapter = new RecyclerViewAdapter(getApplicationContext(), participants);
                        queue.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
