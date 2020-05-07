package com.faisaljaved.myparking.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MessageAdapter";

    private static final int LEFT_MESSAGE = 1;
    private static final int RIGHT_MESSAGE = 2;

    private List<Message> mMessageList;
    private String mUserId;
    private OnDataClickListener mOnDataClickListener;

    public MessageAdapter(OnDataClickListener mOnDataClickListener) {
        this.mOnDataClickListener = mOnDataClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        switch (viewType){
            case LEFT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_item_layout, parent, false);
                return new MessageViewHolder(view, mOnDataClickListener);

            case RIGHT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_item_layout, parent, false);
                return new MessageViewHolder(view, mOnDataClickListener);

             default:
                 view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_item_layout, parent, false);
                 return new MessageViewHolder(view, mOnDataClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(Long.parseLong(mMessageList.get(position).getTimestamp()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String datetime = dateFormat.format(cal1.getTime());

        int itemViewType = getItemViewType(position);
        if (itemViewType == RIGHT_MESSAGE){
            if (mMessageList.get(position).getType().equals("text")){

                ((MessageViewHolder)holder).timestamp.setText(datetime);
                ((MessageViewHolder)holder).textMessage.setText(mMessageList.get(position).getMessage());
                ((MessageViewHolder)holder).textMessage.setVisibility(View.VISIBLE);
                ((MessageViewHolder)holder).imageMessage.setVisibility(View.GONE);
            }
            else if (mMessageList.get(position).getType().equals("image")){

                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background).override(100);

                Glide.with(holder.itemView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(mMessageList.get(position).getMessage())
                        .into(((MessageViewHolder) holder).imageMessage);

                ((MessageViewHolder)holder).timestamp.setText(datetime);
                ((MessageViewHolder)holder).textMessage.setVisibility(View.GONE);
                ((MessageViewHolder)holder).imageMessage.setVisibility(View.VISIBLE);
            }

        }
        else if (itemViewType == LEFT_MESSAGE){
            if (mMessageList.get(position).getType().equals("text")){

                ((MessageViewHolder)holder).timestamp.setText(datetime);
                ((MessageViewHolder)holder).textMessage.setText(mMessageList.get(position).getMessage());
                ((MessageViewHolder)holder).textMessage.setVisibility(View.VISIBLE);
                ((MessageViewHolder)holder).imageMessage.setVisibility(View.GONE);
            }
            else if (mMessageList.get(position).getType().equals("image")){

                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background).override(100);

                Glide.with(holder.itemView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(mMessageList.get(position).getMessage())
                        .into(((MessageViewHolder) holder).imageMessage);

                ((MessageViewHolder)holder).timestamp.setText(datetime);
                ((MessageViewHolder)holder).textMessage.setVisibility(View.GONE);
                ((MessageViewHolder)holder).imageMessage.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (mMessageList != null) {
            return mMessageList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (mMessageList.get(position).getSenderUid().equals(mUserId)){
            return RIGHT_MESSAGE;
        }
        else {
            return LEFT_MESSAGE;
        }
    }

    public void setMessages(List<Message> msglist, String userId) {
        mMessageList = msglist;
        mUserId = userId;
        notifyDataSetChanged();
    }


}
