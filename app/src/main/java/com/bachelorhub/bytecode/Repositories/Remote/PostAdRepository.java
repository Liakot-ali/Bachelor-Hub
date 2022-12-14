package com.bachelorhub.bytecode.Repositories.Remote;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;


import com.bachelorhub.bytecode.Models.Filter;
import com.bachelorhub.bytecode.Models.PostAd;
import com.bachelorhub.bytecode.Repositories.Firebase.FirebaseRepository;

import java.util.ArrayList;

//Singleton pattern
public class PostAdRepository {

    private static final String TAG = "PostAdRepository";
    private static PostAdRepository mInstance;

    public static PostAdRepository getInstance() {
        if (mInstance == null) {
            mInstance = new PostAdRepository();
        }
        return mInstance;
    }

    //Pretend to get data from a webservice or online source
    public MutableLiveData<ArrayList<PostAd>> getAllPostAd() {
        MutableLiveData<ArrayList<PostAd>> data = new MutableLiveData<>();
        new FirebaseRepository().getAllPostAd(new FirebaseRepository.PostsAdCallback() {
            @Override
            public void onCallback(ArrayList<PostAd> arrayList) {
                if (arrayList != null) {
                    //
                }
                //Log.d(TAG, ""+new Gson().toJson(model));
                data.setValue(arrayList);
            }
        });
        return data;
    }

    public MutableLiveData<ArrayList<PostAd>> getAllPostAdByLocation(Filter model) {
        MutableLiveData<ArrayList<PostAd>> data = new MutableLiveData<>();
        new FirebaseRepository().getAllPostAdByLocation(new FirebaseRepository.PostsAdCallback() {
            @Override
            public void onCallback(ArrayList<PostAd> arrayList) {
                data.setValue(arrayList);
            }
        }, model);
        return data;
    }

    public MutableLiveData<PostAd> getPostAdById(String propertyId) {
        MutableLiveData<PostAd> data = new MutableLiveData<>();
        new FirebaseRepository().getPostAdById(new FirebaseRepository.PostAdCallback() {
            @Override
            public void onCallback(PostAd model) {
                //Log.d(TAG, ""+new Gson().toJson(model));
                data.setValue(model);
            }
        }, propertyId);
        return data;
    }

    public MutableLiveData<String> storePostAd(PostAd postAd) {
        MutableLiveData<String> data = new MutableLiveData<>();
        new FirebaseRepository().storePostAd(new FirebaseRepository.StorePostAdCallback() {
            @Override
            public void onCallback(String result) {
                if (result != null) {
                    data.setValue(result);
                }
            }
        }, postAd);
        return data;
    }

    public MutableLiveData<String> storeImage(Uri uri, String directory, String mAuthId) {
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
