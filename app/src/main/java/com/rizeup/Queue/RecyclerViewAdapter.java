package com.rizeup.Queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rizeup.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ParticipantView> {

    private Context mContext;
    private ArrayList<String> participant;
    private ArrayList<Integer> images;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> participant, ArrayList<Integer> images) {
        this.mContext = mContext;
        this.participant = participant;
        this.images = images;
    }

    @NonNull
    @Override
    public ParticipantView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ParticipantView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantView holder, int position) {
        // TODO: Complete this method after Firebase is connected
        holder.setImage(images.get(position));
        holder.setParticipantName(participant.get(position));
    }

    @Override
    public int getItemCount() {
        return participant.size();
    }
}
