package com.rizeup.Queue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.R;
import com.rizeup.utils.User;

import java.util.ArrayList;

public class QueueActivity extends AppCompatActivity {


    private DatabaseReference mDataRef;
    private RecyclerView queue;
    private RecyclerViewAdapter adapter;
    private ArrayList<User> participants;

    private static final String TAG = "Queue.QueueActivity";

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        this.participants = new ArrayList<>();
        this.queue = findViewById(R.id.queue_recycler_view);
        this.queue.setHasFixedSize(true);
        this.queue.setLayoutManager(new LinearLayoutManager(this));
        this.mDataRef = FirebaseDatabase.getInstance().getReference("Users");

        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    participants.add(user);
                }
                adapter = new RecyclerViewAdapter(getApplicationContext(), participants);
                queue.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        itemTouchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                participants.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(queue);
    }
}
