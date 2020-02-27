package com.rizeup.Queue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class QueueActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("The_Queue");

    private RecyclerView queue;
    private RecyclerViewAdapter adapter;
    private ArrayList<Integer> imagesToLoad;
    private ArrayList<String> names;

    private static final String TAG = "Queue.QueueActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        imagesToLoad = new ArrayList<>();
        names = new ArrayList<>();
        this.queue = findViewById(R.id.queue_recycler_view);
//        myRef.setValue("Hello, World!");
//
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.d(TAG, "Failed to read value.", error.toException());
//            }
//        });
        initParticipants();
    }

    private void initParticipants(){
        Log.d(TAG, "initParticipants: preparing Recycler View");
        names.add("user1");
        names.add("user2");
        names.add("user3");
        names.add("user4");
        names.add("user5");
        names.add("user6");

        imagesToLoad.add(R.drawable.user_one);
        imagesToLoad.add(R.drawable.user_two);
        imagesToLoad.add(R.drawable.user_three);
        imagesToLoad.add(R.drawable.user_four);
        imagesToLoad.add(R.drawable.user_five);
        imagesToLoad.add(R.drawable.user_six);
        initRecyclerView();
    }

    private void initRecyclerView(){
        this.adapter = new RecyclerViewAdapter(this,names,imagesToLoad);
        queue.setAdapter(this.adapter);
        queue.setLayoutManager(new LinearLayoutManager(this));
    }
}
