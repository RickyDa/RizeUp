package com.rizeup.Queue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ParticipantView> {

    private Context mContext;
    private ArrayList<QueueParticipant> participant;
    private DatabaseReference userRef;

    public ParticipantRecyclerViewAdapter(Context mContext, ArrayList<QueueParticipant> participant) {
        this.mContext = mContext;
        this.participant = participant;
        this.userRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);

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
                holder.participantName.setText(user.getName());
                holder.participantNumber.setText(String.valueOf(position));
                if (user.getImageUri() == null) {
                    holder.image.setImageResource(R.drawable.defaultimage);
                } else {
                    Glide.with(mContext).asBitmap().load(user.getImageUri()).into(holder.image);
                }

                if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    // TODO change background
                    holder.participantLayout.setBackgroundColor(R.drawable.googleg_standard_color_18);
                }
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
