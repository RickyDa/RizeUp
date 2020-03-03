package com.rizeup.FindQueue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizeup.CreateQueue.RizeUpQueue;
import com.rizeup.R;
import com.rizeup.User;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

public class QueueListRecyclerViewAdapter  extends RecyclerView.Adapter<QueueListRecyclerViewAdapter.QueueHolder>{
    private static final String TAG = "QueueListRecyclerViewAd";
    private Context mContext;
    private ArrayList<RizeUpQueue> queues;

    public QueueListRecyclerViewAdapter(Context mContext, ArrayList<RizeUpQueue> queues) {
        this.mContext = mContext;
        this.queues = queues;
    }

    @NonNull
    @Override
    public QueueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_queueitem, parent, false);
        return new QueueListRecyclerViewAdapter.QueueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueHolder holder, int position) {
        User owner = queues.get(position).getOwner();
        holder.queueName.setText(queues.get(position).getName());
        holder.queueId.setText(queues.get(position).getName()+queues.get(position).getKey());
        if(!(owner.getImageUrl().trim().equals("")))
            Glide.with(mContext).asBitmap().load(queues.get(position).getOwner().getImageUrl()).into(holder.ownerImage);
    }

    @Override
    public int getItemCount() {
        return queues.size();
    }

    public class QueueHolder extends RecyclerView.ViewHolder{

        CircleImageView ownerImage;
        TextView queueName;
        TextView queueId;
        RelativeLayout queueLayout;

        public QueueHolder(@NonNull View itemView) {
            super(itemView);
            ownerImage = itemView.findViewById(R.id.queue_image);
            queueName = itemView.findViewById(R.id.queue_name);
            queueId = itemView.findViewById(R.id.queue_ref);
            queueLayout = itemView.findViewById(R.id.queue_layout);
        }

    }
}
