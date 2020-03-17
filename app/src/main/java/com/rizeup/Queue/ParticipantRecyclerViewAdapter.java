package com.rizeup.Queue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.rizeup.models.RiZeUpUser;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ParticipantView> {

    private AppCompatActivity mContext;
    private ArrayList<QueueParticipant> participant;
    private DatabaseReference userRef;
    private DatabaseReference queueRef;

    ParticipantRecyclerViewAdapter(AppCompatActivity mContext, ArrayList<QueueParticipant> participant) {
        this.mContext = mContext;
        this.participant = participant;
        this.userRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);
        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);
    }

    @NonNull
    @Override
    public ParticipantView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_participantitem, parent, false);
        return new ParticipantView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParticipantView holder, final int position) {
        this.userRef.child(participant.get(position).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final RiZeUpUser user = dataSnapshot.getValue(RiZeUpUser.class);
                if(user != null) {
                    holder.participantName.setText(user.getName());
                    holder.participantNumber.setText(String.valueOf(position));
                    if (user.getImageUri().trim().equals("")) {
                        Glide.with(mContext).asBitmap().load(R.drawable.defaultimage).into(holder.image);
                    } else {
                        Glide.with(mContext).asBitmap().load(user.getImageUri()).into(holder.image);
                    }

                    if (user.getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        FloatingActionButton fab = holder.participantLayout.findViewById(R.id.participant_deleteBtn);
                        fab.setVisibility(View.VISIBLE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openApprovalDialog(holder);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void openApprovalDialog(final ParticipantView holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Are you sure?").setMessage("By pressing \"yes\" you will be out of the queue.").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser();
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        builder.create().show();
    }

    private void deleteUser() {
        this.userRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/"
                +FirebaseReferences.REAL_TIME_RIZE_UP_USER_REG)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deleteFromQueue(dataSnapshot.getValue(String.class));
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void deleteFromQueue(String value) {
        this.queueRef.child(value+"/"+FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mContext.finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return participant.size();
    }

    static class ParticipantView extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView participantName;
        TextView participantNumber;
        LinearLayout participantLayout;

        ParticipantView(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.participant_image);
            this.participantName = itemView.findViewById(R.id.participant_name);
            this.participantNumber = itemView.findViewById(R.id.participant_number);
            this.participantLayout = itemView.findViewById(R.id.participant_layout);
        }
    }
}
