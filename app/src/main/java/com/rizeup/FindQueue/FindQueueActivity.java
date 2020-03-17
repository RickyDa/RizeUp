package com.rizeup.FindQueue;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.R;
import com.rizeup.models.RiZeUpQueue;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;

public class FindQueueActivity extends AppCompatActivity implements ValueEventListener, ChildEventListener {

    public static final String QID_EXTRA = "qid";

    private RecyclerView queueListRecyclerView;
    private ArrayList<RiZeUpQueue> qList;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_queue);

        this.loading = new ProgressDialog(this);
        this.loading.setCancelable(false);
        this.loading.setInverseBackgroundForced(false);
        this.loading.show();

        this.qList = new ArrayList<>();
        this.queueListRecyclerView = findViewById(R.id.recyclerViewQList);
        this.queueListRecyclerView.setHasFixedSize(true);
        this.queueListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseQueueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);

        databaseQueueRef.addValueEventListener(this);
        databaseQueueRef.addChildEventListener(this);
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            RiZeUpQueue q = snapshot.getValue(RiZeUpQueue.class);
            if (!(q.getOwnerUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) || qList.contains(q))) {
                qList.add(q);
            }
        }
        QueueListRecyclerViewAdapter qListAdapter = new QueueListRecyclerViewAdapter(FindQueueActivity.this, qList);
        queueListRecyclerView.setAdapter(qListAdapter);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (loading.isShowing()) {
            loading.dismiss();
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
