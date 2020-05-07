package com.faisaljaved.myparking.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.faisaljaved.myparking.AppExecuters;
import com.faisaljaved.myparking.models.ChatUsers;
import com.faisaljaved.myparking.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.faisaljaved.myparking.utils.Constants.NETWORK_TIMEOUT;

public class MessageRepository {

    private static final String TAG = "MessageRepository";

    private static MessageRepository instance;

    private MutableLiveData<List<Message>> mMessageList;

    public static MessageRepository getInstance(){

        if (instance == null){
            instance = new MessageRepository();
        }

        return instance;
    }

    public MessageRepository() {
        mMessageList = new MutableLiveData<>();
    }

    public LiveData<List<Message>> getMessages(){
        return mMessageList;
    }

    public void loadMessagesFromFirebase(final String userId, final String receiverUid, final String receiverAdId) {
        final Future handler = AppExecuters.getInstance().networkIO().submit(new Runnable() {
            @Override
            public void run() {
                //retrieve data from firebase
                final List<Message> msglist = new ArrayList<>();

                final String childId = userId+receiverUid+receiverAdId;
                final String childIdrev = receiverUid+userId+receiverAdId;

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chatMessages");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        msglist.clear();
                        if (dataSnapshot.child(childId).exists()){
                            for (DataSnapshot snapshot: dataSnapshot.child(childId).getChildren()) {
                                Message message = snapshot.getValue(Message.class);
                                msglist.add(message);
                            }
                        }
                        else if (dataSnapshot.child(childIdrev).exists()){
                            Log.d(TAG, "onDataChange: ye bhi exist krta hai");
                            for (DataSnapshot snapshot: dataSnapshot.child(childIdrev).getChildren()) {
                                Message message = snapshot.getValue(Message.class);
                                msglist.add(message);
                            }
                        }
                        mMessageList.postValue(msglist);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        AppExecuters.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //let user know its timed out
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);

    }
}
