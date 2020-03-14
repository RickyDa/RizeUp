package com.rizeup.FindQueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.models.RiZeUpQueue;
import com.rizeup.R;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;

public class FindQueueActivity extends AppCompatActivity {
    public static final String QID_EXTRA = "qid";
    private static final String TAG = "FindQueueActivity";

    private RecyclerView queueListRecyclerView;
    private QueueListRecyclerViewAdapter qListAdapter;
    private DatabaseReference databaseQueueRef;
    private ArrayList<RiZeUpQueue> qList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_queue);
        this.qList = new ArrayList<>();
        this.queueListRecyclerView = findViewById(R.id.recyclerViewQList);
        this.queueListRecyclerView.setHasFixedSize(true);
        this.queueListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.databaseQueueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);

        databaseQueueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RiZeUpQueue q = snapshot.getValue(RiZeUpQueue.class);
                    if(!qList.contains(q))
                        qList.add(q);
            }
                qListAdapter = new QueueListRecyclerViewAdapter(getApplicationContext(), qList);
                queueListRecyclerView.setAdapter(qListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
