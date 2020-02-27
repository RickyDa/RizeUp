package com.rizeup.Queue;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rizeup.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantView extends RecyclerView.ViewHolder {
    private static final String TAG = "Queue.ParticipantView";

    private CircleImageView image;
    private TextView participantName;
    private RelativeLayout participantLayout;

    public ParticipantView(View itemView) {
        super(itemView);
        this.image = itemView.findViewById(R.id.participant_image);
        this.participantName = itemView.findViewById(R.id.participant_name);
        this.participantLayout = itemView.findViewById(R.id.participant_layout);
    }
}
