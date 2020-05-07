package com.faisaljaved.myparking.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.listener.OnDataClickListener;

public class ChatUsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    AppCompatImageView mAdImage, mUserImage;
    TextView mUser, mAdTitle;
    OnDataClickListener mOnDataClickListener;

    public ChatUsersListViewHolder(@NonNull View itemView, OnDataClickListener onDataClickListener) {
        super(itemView);

        this.mOnDataClickListener = onDataClickListener;

        mAdImage = itemView.findViewById(R.id.chat_item_ad_image);
        mUserImage = itemView.findViewById(R.id.chat_item_user_image);
        mUser = itemView.findViewById(R.id.chat_item_username);
        mAdTitle = itemView.findViewById(R.id.chat_item_ad_title);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mOnDataClickListener.onItemClick(getAdapterPosition());
    }
}
