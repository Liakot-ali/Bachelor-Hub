package com.bachelorhub.bytecode.Repositories.retrofit;



import com.bachelorhub.bytecode.Models.notification.RootModel;
import com.bachelorhub.bytecode.utils.ConstantKey;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers({"Authorization: key=" + ConstantKey.SERVER_KEY, "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);

}

