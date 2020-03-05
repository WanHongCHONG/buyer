package com.harshit.androideatitserver.Remote;

import com.harshit.androideatitserver.Model.MyResponse;
import com.harshit.androideatitserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAsMsCKw4:APA91bFUhSOCCEmtY9uTIeiOU7iXviBNG6LdLsCF_Mcs50AHBsQ6dYONvwAiAp45nP617CGmWu0S9sTI8olXJniMGHeO6qPfxvuDUkJZ42S19JTIP_KYhO-U5q1O19LJ_Xh9Z3nE7KS_"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
