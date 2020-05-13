package com.faisaljaved.myparking.adapters;

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
import com.faisaljaved.myparking.models.ChatUsers;

import java.util.List;


public class ChatUsersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ChatUsersListAdapter";

    private List<ChatUsers> mBChatUserList;
    private List<ChatUsers> mSChatUserList;
    private OnDataClickListener mOnDataClickListener;

    public ChatUsersListAdapter(OnDataClickListener mOnDataClickListener) {
        this.mOnDataClickListener = mOnDataClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_item_layout, parent, false);
        return new ChatUsersListViewHolder(view, mOnDataClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.blank_image).override(100, 100).apply(RequestOptions.circleCropTransform());

        //buying
        if (mBChatUserList != null) {

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(mBChatUserList.get(position).getAdImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(((ChatUsersListViewHolder) holder).mAdImage);

            if (!mBChatUserList.get(position).getSellerImage().equals("default")) {
                Glide.with(holder.itemView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(mBChatUserList.get(position).getSellerImage())
                        .into(((ChatUsersListViewHolder) holder).mUserImage);
            }

            Log.d(TAG, "onBindViewHolder: tag" + mBChatUserList.get(position).getSellerImage());
            ((ChatUsersListViewHolder) holder).mUser.setText(mBChatUserList.get(position).getSellerUsername());
            ((ChatUsersListViewHolder) holder).mAdTitle.setText(mBChatUserList.get(position).getAdTitle());
        }

        //selling
        if (mSChatUserList != null) {

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(mSChatUserList.get(position).getAdImage())
                    .into(((ChatUsersListViewHolder) holder).mAdImage);

            if (!mSChatUserList.get(position).getSellerImage().equals("default")) {
                Glide.with(holder.itemView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(mSChatUserList.get(position).getBuyerImage())
                        .into(((ChatUsersListViewHolder) holder).mUserImage);
            }

            ((ChatUsersListViewHolder) holder).mUser.setText(mSChatUserList.get(position).getBuyerUsername());
            ((ChatUsersListViewHolder) holder).mAdTitle.setText(mSChatUserList.get(position).getAdTitle());
        }

    }

    @Override
    public int getItemCount() {
        if (mBChatUserList != null) {
            mSChatUserList = null;
            return mBChatUserList.size();
        }
        else if(mSChatUserList != null){
            mBChatUserList = null;
            return mSChatUserList.size();
        }
        return 0;
    }

    public void setBuyingUserData(List<ChatUsers> usersList) {
        mBChatUserList = usersList;
        notifyDataSetChanged();
    }

    public void setSellingUserData(List<ChatUsers> usersList) {
        mSChatUserList = usersList;
        notifyDataSetChanged();
    }

    public ChatUsers getSelectedBUserData(int position){

        if (mBChatUserList != null){
            if (mBChatUserList.size() > 0){
                return mBChatUserList.get(position);
            }
        }
        return null;
    }

    public ChatUsers getSelectedSUserData(int position){

        if (mSChatUserList != null){
            if (mSChatUserList.size() > 0){
                return mSChatUserList.get(position);
            }
        }
        return null;
    }

}
