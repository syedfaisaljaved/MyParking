package com.faisaljaved.myparking.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.listener.OnDataClickListener;

public class AdDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView mAdPrice, mAdTitle, mAdLocation, mAdTimeStamp, mAdVehicleType;
    AppCompatImageView mAdImage;
    OnDataClickListener mOnDataClickListener;

    public AdDataViewHolder(@NonNull View itemView, OnDataClickListener onDataClickListener) {
        super(itemView);
        this.mOnDataClickListener = onDataClickListener;

        mAdPrice = (TextView) itemView.findViewById(R.id.ad_price);
        mAdTitle = (TextView) itemView.findViewById(R.id.ad_title);
        mAdVehicleType = (TextView) itemView.findViewById(R.id.ad_vehicle_type);
        mAdLocation = (TextView) itemView.findViewById(R.id.ad_location);
        mAdTimeStamp = (TextView) itemView.findViewById(R.id.ad_timestamp);
        mAdImage = (AppCompatImageView) itemView.findViewById(R.id.ad_image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mOnDataClickListener.onItemClick(getAdapterPosition());
    }
}
