package com.rizeup.FindQueue;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.Queue.QueueActivity;
import com.rizeup.R;
import com.rizeup.models.QueueParticipant;
import com.rizeup.models.RiZeUpQueue;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class QueueListRecyclerViewAdapter extends RecyclerView.Adapter<QueueListRecyclerViewAdapter.QueueHolder> {

    private AppCompatActivity mContext;
    private ArrayList<RiZeUpQueue> queues;

    QueueListRecyclerViewAdapter(AppCompatActivity mContext, ArrayList<RiZeUpQueue> queues) {
        this.mContext = mContext;
        this.queues = queues;
    }

    @NonNull
    @Override
    public QueueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_queueitem, parent, false);
        return new QueueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final QueueHolder holder, final int position) {
        final RiZeUpQueue q = queues.get(position);
        holder.queueName.setText(q.getName());
        holder.queueOwner.setText(q.getOwnerName());
        holder.id = q.getOwnerUid();

        if (!(q.getImageUrl().trim().equals(""))) {
            Glide.with(mContext).asBitmap().load(queues.get(position).getImageUrl()).into(holder.queueImage);
        }

        holder.qRef = holder.qRef.child(q.getOwnerUid() + "/" + FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);
        holder.qRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.queueNumOfParticipant.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.queueLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(queues.get(position).getLat(), queues.get(position).getLng(), queues.get(position).getName(), queues.get(position).getImageUrl());
            }
        });


    }

    private void openDialog(double lat, double lng, String name, String imageUrl) {
        MapDialog md = new MapDialog(lat, lng, name, imageUrl);
        md.show(mContext.getSupportFragmentManager(),"QUEUE");
    }


    @Override
    public int getItemCount() {
        return queues.size();
    }

    public static class QueueHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ValueEventListener {

        CircleImageView queueImage;
        TextView queueName;
        TextView queueOwner;
        LinearLayout queueLayout;
        Button queueLocation;
        TextView queueNumOfParticipant;
        Context context;
        String id;
        DatabaseReference qRef;
        DatabaseReference userRef;

        QueueHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            queueImage = itemView.findViewById(R.id.queue_image);
            queueName = itemView.findViewById(R.id.queue_name);
            queueOwner = itemView.findViewById(R.id.queue_owner);
            queueLayout = itemView.findViewById(R.id.queue_layout);
            queueLocation = itemView.findViewById(R.id.queueItemBtn);
            queueNumOfParticipant = itemView.findViewById(R.id.queueItem_numOfParticipant);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            qRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);
            userRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS + "/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        }

        @Override
        public void onClick(View v) {
            this.userRef.addListenerForSingleValueEvent(this);
        }

        private void registerUser() {
            final String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            final DatabaseReference queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES).child(id).child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);
            queueRef.child(userId).setValue(new QueueParticipant(userId, System.currentTimeMillis())).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    userRef.child(FirebaseReferences.REAL_TIME_RIZE_UP_USER_REG).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startQueueActivity();
                        }
                    });
                }
            });
        }

        private void startQueueActivity() {
            Intent intent = new Intent(context, QueueActivity.class);
            intent.putExtra(FindQueueActivity.QID_EXTRA, id);
            context.startActivity(intent);
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild(FirebaseReferences.REAL_TIME_RIZE_UP_USER_REG)) {
                if(!Objects.equals(dataSnapshot.child(FirebaseReferences.REAL_TIME_RIZE_UP_USER_REG).getValue(), id))
                    Toast.makeText(context, "REGISTRATION DENIED: Your'e already REGISTERED to a queue", Toast.LENGTH_SHORT).show();
                startQueueActivity();
            } else {
                registerUser();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
