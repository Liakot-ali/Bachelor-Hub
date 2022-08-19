package com.bachelorhub.bytecode.Repositories.Remote;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.Repositories.Firebase.FirebaseRepository;


//Singleton pattern
public class UserRepository {

    private static UserRepository mInstance;

    public static UserRepository getInstance() {
        if (mInstance == null) {
            mInstance = new UserRepository();
        }
        return mInstance;
    }

    //Pretend to get data from a webservice or online source
    public MutableLiveData<User> getUser(String mAuthId) {
        MutableLiveData<User> data = new MutableLiveData<>();
        new FirebaseRepository().getUserData(new FirebaseRepository.UserCallback() {
            @Override
            public void onCallback(User model) {
                Log.d("UserRepository", "getUser : " + new Gson().toJson(model));
                data.setValue(model);
            }
        }, mAuthId);
        return data;
    }

    public MutableLiveData<String> storeUser(User user) {
        MutableLiveData<String> data = new MutableLiveData<>();
        new FirebaseRepository().storeUserData(new FirebaseRepository.UserStoreCallback() {
            @Override
            public void onCallback(String result) {
                if (result != null) {
                    data.setValue(result);
                }
            }
        }, user);
        return data;
    }

    public MutableLiveData<String> storeUserImage(Uri uri, String directory, String mAuthId) {
        MutableLiveData<String> data = new MutableLiveData<>();
        new FirebaseRepository().uploadImageToStorage(new FirebaseRepository.ImageStorageCallback() {
            @Override
            public void onCallback(String result) {
                data.setValue(result);
            }
        }, uri, directory, mAuthId);
        return data;
    }
}
