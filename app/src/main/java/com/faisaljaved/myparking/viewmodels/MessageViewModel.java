package com.faisaljaved.myparking.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.faisaljaved.myparking.models.ChatUsers;
import com.faisaljaved.myparking.models.Message;
import com.faisaljaved.myparking.repositories.ChatUsersRepository;
import com.faisaljaved.myparking.repositories.MessageRepository;

import java.util.List;

public class MessageViewModel extends ViewModel {

    private MessageRepository mMessageRepository;

    public MessageViewModel(){
        mMessageRepository = MessageRepository.getInstance();
    }

    public LiveData<List<Message>> getMessages(){
        return mMessageRepository.getMessages();
    }

    public void loadMessagesFromFirebase(String userId, String receiverUid, String receiverAdId){
        mMessageRepository.loadMessagesFromFirebase(userId, receiverUid, receiverAdId);
    }
}
