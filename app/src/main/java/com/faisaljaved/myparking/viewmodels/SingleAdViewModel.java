package com.faisaljaved.myparking.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.faisaljaved.myparking.models.MyAdData;
import com.faisaljaved.myparking.models.UserDetails;
import com.faisaljaved.myparking.repositories.AdDataRepository;

public class SingleAdViewModel extends ViewModel {

    private AdDataRepository mAdDataRepository;

    private String mAdID;

    public SingleAdViewModel() {
        this.mAdDataRepository = AdDataRepository.getInstance();
    }

    public LiveData<MyAdData> getData(){
        return mAdDataRepository.getSingleAdData();
    }

    public LiveData<UserDetails> getUserDetails(){
        return mAdDataRepository.getUserDetails();
    }

    public void loadSingleAdFromFirebase(String uid, String adId){
        mAdID = adId;
        mAdDataRepository.loadSingleAdFromFirebase(uid, adId);
    }

    public String getAdId() {
        return mAdID;
    }
}
