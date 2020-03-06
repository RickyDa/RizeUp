package com.rizeup.Queue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizeup.R;
import com.rizeup.SignUp.RiZeUpUser;
import com.rizeup.utils.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ParticipantView> {

    private Context mContext;
    private ArrayList<RiZeUpUser> participant;

    public RecyclerViewAdapter(Context mContext, ArrayList<RiZeUpUser> participant) {
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
    public void onBindViewHolder(@NonNull ParticipantView holder, int position) {
        // TODO: Complete this method after Firebase is connected

        holder.participantName.setText(participant.get(position).getName());
        holder.participantNumber.setText(String.valueOf(position));
        Glide.with(mContext).asBitmap().load(participant.get(position).getImageUri()).into(holder.image);
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
