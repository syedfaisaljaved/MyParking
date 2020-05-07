package com.faisaljaved.myparking.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.faisaljaved.myparking.models.ChatUsers;
import com.faisaljaved.myparking.repositories.ChatUsersRepository;

import java.util.List;

public class ChatUsersViewModel extends ViewModel {

    private ChatUsersRepository mChatUsersRepository;

    public ChatUsersViewModel(){
        mChatUsersRepository = ChatUsersRepository.getInstance();
    }

    public LiveData<List<ChatUsers>> getBuyersList(){
        return mChatUsersRepository.getBuyingUsersList();
    }

    public LiveData<List<ChatUsers>> getSellersList(){
        return mChatUsersRepository.getSellingUsersList();
    }

    public void loadBuyingUsersFromFirebase(String userId){
        mChatUsersRepository.loadBuyingUsersFromFirebase(userId);
    }

    public void loadSellingUsersFromFirebase(String userId){
        mChatUsersRepository.loadSellingUsersFromFirebase(userId);
    }
}
