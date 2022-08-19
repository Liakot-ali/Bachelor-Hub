package com.bachelorhub.bytecode.Repositories.Firebase;

import android.net.Uri;


import com.bachelorhub.bytecode.Models.Feedback;
import com.bachelorhub.bytecode.Models.Filter;
import com.bachelorhub.bytecode.Models.PostAd;
import com.bachelorhub.bytecode.Models.User;

import java.util.ArrayList;

public interface IFirebaseRepository {

    //===============================================| Get

    User getUserById(String mAuthId);
    ArrayList<PostAd> getPostAdList();
    ArrayList<PostAd> getAllPostAdByFilter(Filter mFilter);
    PostAd getPostAdById(String mAuthId);
    ArrayList<User> getAllNotificationById(String mAuthId);
    ArrayList<Feedback> getAllFeedback(String mAuthId);

    //===============================================| Store

    String saveUser(User mUser);
    String savePostAd(PostAd mPostAd);
    String saveNotification(String mOwnerAuthId, User mUser);
    String saveFeedback(Feedback mFeedback);

    //===============================================| Image Storage

    String saveImageByPath(Uri uri, String mDirectory, String mAuthId);

    //===============================================| Remove

    String deleteFeedbackById(String mAuthId);
    String deletePostAdById(String mOwnerAuthId);
    String deleteImageByUrl(String mImageUrl);

}
