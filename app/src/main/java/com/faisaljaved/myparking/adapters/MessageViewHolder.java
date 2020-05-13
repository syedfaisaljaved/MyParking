package com.faisaljaved.myparking.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.listener.OnDataClickListener;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView textMessage, timestamp;
    ImageView imageMessage;
    OnDataClickListener mOnDataClickListener;

    public MessageViewHolder(@NonNull View itemView, OnDataClickListener onDataClickListener) {
        super(itemView);

        this.mOnDataClickListener = onDataClickListener;

        timestamp = itemView.findViewById(R.id.text_timestamp);
        textMessage = itemView.findViewById(R.id.text_message);
        imageMessage = itemView.findViewById(R.id.image_message);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mOnDataClickListener.onItemClick(getAdapterPosition());
    }
}
