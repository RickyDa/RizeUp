package com.rizeup.Queue;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.R;
import com.rizeup.models.QueueParticipant;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;

public class ManageActivity extends RiZeUpQueueActivity {

    private DatabaseReference usersRef;
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
        this.usersRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);
        this.participantsRef = queueRef.child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);

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
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(participants.get(i).getUid())) {
                                snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        usersRef.child(snapshot.getKey()+"/"+ FirebaseReferences.REAL_TIME_RIZE_UP_USER_REG).removeValue();
                                    }
                                });
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
