package com.rizeup.Queue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizeup.R;
import com.rizeup.SignUp.RiZeUpUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantRecyclerViewAdapter extends RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ParticipantView> {

    private Context mContext;
    private ArrayList<RiZeUpUser> participant;

    public ParticipantRecyclerViewAdapter(Context mContext, ArrayList<RiZeUpUser> participant) {
        this.mContext = mContext;
        this.participant = participant;

    }

    @NonNull
    @Override
    public ParticipantView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_participantitem, parent, false);
        return new ParticipantView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantView holder, final int position) {
        holder.participantName.setText(participant.get(position).getName());
        holder.participantNumber.setText(String.valueOf(position));

        if (participant.get(position).getImageUri() == null) {
            holder.image.setImageResource(R.drawable.defaultimage);
        } else {
            Glide.with(mContext).asBitmap().load(participant.get(position).getImageUri()).into(holder.image);
        }

        holder.participantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, participant.get(position).getUid(), Toast.LENGTH_SHORT).show();
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
