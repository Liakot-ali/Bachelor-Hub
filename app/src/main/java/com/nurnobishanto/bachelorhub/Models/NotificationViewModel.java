package com.nurnobishanto.bachelorhub.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.nurnobishanto.bachelorhub.Repositories.Remote.NotificationRepository;

import java.util.ArrayList;

public class NotificationViewModel extends ViewModel {

    private NotificationRepository mRepository;

    public NotificationViewModel() {
        this.mRepository = NotificationRepository.getInstance();
    }

    public LiveData<ArrayList<User>> getNotifications(String mAuthId) {
        MutableLiveData<ArrayList<User>> users = mRepository.getNotifications(mAuthId);
        return users;
    }

}
