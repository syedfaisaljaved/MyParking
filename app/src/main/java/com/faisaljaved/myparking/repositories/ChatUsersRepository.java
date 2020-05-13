package com.faisaljaved.myparking.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.faisaljaved.myparking.utils.AppExecuters;
import com.faisaljaved.myparking.models.ChatUsers;
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

public class ChatUsersRepository {

    private static final String TAG = "ChatUsersRepository";

    private static ChatUsersRepository instance;

    private MutableLiveData<List<ChatUsers>> mBuyingUsersList;
    private MutableLiveData<List<ChatUsers>> mSellingUsersList;

    public static ChatUsersRepository  getInstance(){

        if (instance == null){
            instance = new ChatUsersRepository();
        }

        return instance;
    }

    public ChatUsersRepository() {
        mBuyingUsersList = new MutableLiveData<>();
        mSellingUsersList = new MutableLiveData<>();
    }

    public LiveData<List<ChatUsers>> getBuyingUsersList(){
        return mBuyingUsersList;
    }

    public LiveData<List<ChatUsers>> getSellingUsersList(){
        return mSellingUsersList;
    }


    public void loadBuyingUsersFromFirebase(final String userId) {
        final Future handler = AppExecuters.getInstance().networkIO().submit(new Runnable() {
            @Override
            public void run() {
                //retrieve data from firebase
                final List<ChatUsers> buyinglist = new ArrayList<>();

                DatabaseReference buyref = FirebaseDatabase.getInstance().getReference().child("sellers_buyers").child(userId);
                buyref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        buyinglist.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.child("buying").getChildren()) {
//                            Log.d(TAG, "onDataChange: postSnapshot "+ postSnapshot);
                            for (DataSnapshot snapshot : postSnapshot.getChildren()) {
                                ChatUsers details = snapshot.getValue(ChatUsers.class);
                                buyinglist.add(details);
//
                            }
                        }
                        mBuyingUsersList.postValue(buyinglist);
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

    public void loadSellingUsersFromFirebase(final String userId) {
        final Future handler = AppExecuters.getInstance().networkIO().submit(new Runnable() {
            @Override
            public void run() {
                //retrieve data from firebase
                final List<ChatUsers> sellinglist = new ArrayList<>();

                DatabaseReference sellref = FirebaseDatabase.getInstance().getReference().child("sellers_buyers").child(userId);
                sellref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sellinglist.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.child("selling").getChildren()) {
                            for (DataSnapshot snapshot : postSnapshot.getChildren()) {
                                ChatUsers details = snapshot.getValue(ChatUsers.class);
                                sellinglist.add(details);
                            }
                        }
                        mSellingUsersList.postValue(sellinglist);
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
