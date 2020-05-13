package com.faisaljaved.myparking.adapters;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.MyAdData;

import java.text.DecimalFormat;
import java.util.List;


public class AdDataRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AdDataRecyclerAdapter";

    private static final int AD_DATA_TYPE = 1;
    private List<MyAdData> mDatalist;
    private OnDataClickListener mOnDataClickListener;
    private boolean mSwipetoDelete;

    public AdDataRecyclerAdapter(OnDataClickListener onDataClickListener) {
        this.mOnDataClickListener = onDataClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        switch (viewType){

            case AD_DATA_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ad_data_list_item_layout,  parent, false);
                return new AdDataViewHolder(view, mOnDataClickListener);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ad_data_list_item_layout, parent,false);
                return new AdDataViewHolder(view, mOnDataClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: list "+ mDatalist.get(0).getUID());
        int itemViewType = getItemViewType(position);
        if (itemViewType == AD_DATA_TYPE){

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.no_image).override(200,200);

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(mDatalist.get(position).getImages().get(0))
                    .into(((AdDataViewHolder) holder).mAdImage);

            DecimalFormat inr = new DecimalFormat("##,##,##0");
            String price = inr.format(Integer.parseInt(mDatalist.get(position).getPrice()));
            ((AdDataViewHolder)holder).mAdPrice.setText("\u20B9"+price);
            ((AdDataViewHolder)holder).mAdTitle.setText(mDatalist.get(position).getTitle());
            ((AdDataViewHolder) holder).mAdTitle.setSelected(true);
            String vehicletype = "PARKING: "+ mDatalist.get(position).getVehicleType();
            ((AdDataViewHolder) holder).mAdVehicleType.setText(vehicletype);
            ((AdDataViewHolder) holder).mAdLocation.setText(mDatalist.get(position).getLocation());
            ((AdDataViewHolder) holder).mAdLocation.setSelected(true);
            long time = -mDatalist.get(position).getTimestamp();
            CharSequence relativeDate = DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
            ((AdDataViewHolder) holder).mAdTimeStamp.setText(relativeDate);

            if (mSwipetoDelete && position == mDatalist.size()-1){
                ((AdDataViewHolder) holder).swipeToDelete.setVisibility(View.VISIBLE);
            }else {
                ((AdDataViewHolder) holder).swipeToDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mDatalist != null){
            return mDatalist.size();
        }
        return 0;
    }

    public void setAdData(List<MyAdData> adData){
        mDatalist = adData;
        Log.d(TAG, ": "+ mDatalist);
        notifyDataSetChanged();
    }

    public void setSwipetoDelete(boolean value){
        if (mDatalist != null){
            if (mDatalist.size()>0){
                mSwipetoDelete = value;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return AD_DATA_TYPE;
    }

    public MyAdData getSelectedAdData(int position){
        if (mDatalist != null){
            if (mDatalist.size() > 0){
                return mDatalist.get(position);
            }
        }
        return null;
    }
}
