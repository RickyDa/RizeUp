package com.rizeup.FindQueue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rizeup.CreateQueue.RizeUpQueue;
import com.rizeup.R;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

public class QueueListRecyclerViewAdapter extends RecyclerView.Adapter<QueueListRecyclerViewAdapter.QueueHolder> {
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
        final RizeUpQueue q = queues.get(position);
        holder.queueName.setText(q.getName());
        holder.queueOwner.setText(q.getOwnerName());
        if (!(q.getImageUrl().trim().equals("")))
            Glide.with(mContext).asBitmap().load(queues.get(position).getImageUrl()).into(holder.queueImage);
        holder.queueLayout.setOnClickListener(new View.OnClickListener() {
            //TODO: change action to open MAP
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "lat: " + q.getLat() + "lng:" + q.getLng(),Toast.LENGTH_SHORT ).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return queues.size();
    }

    public class QueueHolder extends RecyclerView.ViewHolder {

        CircleImageView queueImage;
        TextView queueName;
        TextView queueOwner;
        RelativeLayout queueLayout;

        public QueueHolder(@NonNull View itemView) {
            super(itemView);
            queueImage = itemView.findViewById(R.id.queue_image);
            queueName = itemView.findViewById(R.id.queue_name);
            queueOwner = itemView.findViewById(R.id.queue_owner);
            queueLayout = itemView.findViewById(R.id.queue_layout);
        }

    }
}
