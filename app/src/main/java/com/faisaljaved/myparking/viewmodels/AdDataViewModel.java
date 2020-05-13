package com.faisaljaved.myparking.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.faisaljaved.myparking.models.MyAdData;
import com.faisaljaved.myparking.repositories.AdDataRepository;

import java.util.List;

public class AdDataViewModel extends ViewModel {

    private AdDataRepository mAdDataRepository;

    public AdDataViewModel() {
        mAdDataRepository = AdDataRepository.getInstance();
    }

    public LiveData<List<MyAdData>> getAdData(){
        return mAdDataRepository.getAdData() ;
    }

    public LiveData<List<MyAdData>> getMyAdData(){
        return mAdDataRepository.getMyAdData() ;
    }

    public void loadDataFromFirebase(){
        mAdDataRepository.loadDataFromFirebase();
    }

    public void loadMyAdDataFromFirebase(String userId){
        mAdDataRepository.loadMyAdDataFromFirebase(userId);
    }

    public void getMoreData(){
        mAdDataRepository.getMoreData();
    }
}
