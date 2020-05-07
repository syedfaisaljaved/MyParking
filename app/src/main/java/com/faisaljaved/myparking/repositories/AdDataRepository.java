package com.faisaljaved.myparking.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.faisaljaved.myparking.AppExecuters;
import com.faisaljaved.myparking.models.MyAdData;
import com.faisaljaved.myparking.models.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.faisaljaved.myparking.utils.Constants.NETWORK_TIMEOUT;

public class AdDataRepository {

    private static final String TAG = "AdDataRepository";

    private static AdDataRepository instance;
    private MutableLiveData<List<MyAdData>> mAdData;
    private MutableLiveData<List<MyAdData>> mMyAdData;
    private MutableLiveData<MyAdData> mSingleAdData;
    private MutableLiveData<UserDetails> mUserDetails;
    private static final int LOAD_DATA_SIZE = 3;

    public static AdDataRepository getInstance(){
        if (instance == null){
            instance = new AdDataRepository();
        }
        return instance;
    }

    private AdDataRepository() {
        mAdData = new MutableLiveData<>();
        mMyAdData = new MutableLiveData<>();
        mSingleAdData = new MutableLiveData<>();
        mUserDetails = new MutableLiveData<>();
    }

    public LiveData<List<MyAdData>> getAdData(){
        return mAdData;
    }

    public LiveData<List<MyAdData>> getMyAdData(){
        return mMyAdData;
    }

    public LiveData<MyAdData> getSingleAdData(){
        return mSingleAdData;
    }

    public LiveData<UserDetails> getUserDetails(){
        return mUserDetails;
    }

    public void loadDataFromFirebase() {
        final Future handler = AppExecuters.getInstance().networkIO().submit(new Runnable() {
            @Override
            public void run() {
                //retrieve data from firebase
                final List<MyAdData> adDataList = new ArrayList<>();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("data");
                Query query = reference.orderByChild("MyAds").limitToLast(LOAD_DATA_SIZE);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: count "+ dataSnapshot.getChildrenCount());
                        adDataList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshot: postSnapshot.child("MyAds").getChildren()) {
                                MyAdData adDataSnapshot = snapshot.getValue(MyAdData.class);
                                adDataList.add(adDataSnapshot);

                                Log.d(TAG, "onDataChange: timestamp"+snapshot);
                            }
                        }
                        Collections.reverse(adDataList);
                        mAdData.postValue(adDataList);
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

    public void loadMyAdDataFromFirebase(final String userId) {
        final Future handler = AppExecuters.getInstance().networkIO().submit(new Runnable() {
            @Override
            public void run() {
                //retrieve data from firebase
                final List<MyAdData> adDataList = new ArrayList<>();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("data");
                Query query = reference.orderByChild("timestamp");
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        adDataList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.child(userId).child("MyAds").getChildren()) {
                            MyAdData adDataSnapshot = postSnapshot.getValue(MyAdData.class);
                            adDataList.add(adDataSnapshot);
                            Log.d(TAG, "onDataChange: timestamp"+postSnapshot);
                        }
                        Collections.reverse(adDataList);
                        mMyAdData.postValue(adDataList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                query.addValueEventListener(valueEventListener);

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

    public void loadSingleAdFromFirebase(final String userId, final String adId) {
        final Future handler = AppExecuters.getInstance().networkIO().submit(new Runnable() {
            @Override
            public void run() {
                //retrieve data from firebase
                Query reference = FirebaseDatabase.getInstance().getReference().child("data").child(userId).child("MyAds").child(adId);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MyAdData adDataSnapshot = dataSnapshot.getValue(MyAdData.class);
                        mSingleAdData.postValue(adDataSnapshot);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Query reference2 = FirebaseDatabase.getInstance().getReference().child("userDetails").child(userId);
                reference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDetails adDataSnapshot = dataSnapshot.getValue(UserDetails.class);
                        mUserDetails.postValue(adDataSnapshot);
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
