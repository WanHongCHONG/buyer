package com.bmacode17.androideatit.remotes;


import com.bmacode17.androideatit.models.MyResponse;
import com.bmacode17.androideatit.models.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by User on 14-Aug-18.
 */

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json" ,
                    "Authorization:key=AAAA4EDKgrE:APA91bE7sig2qHDBYMv6ZgKWZoketAnQl6hQ7njVk_bwFLluQ2L3C3i_lb_Vt2xtfMcj2ZNzIHZsrnUdBLFG10S-S6ZbXRxqPWNcEF4JgzZyr8197edwc5ebJ5VNhG4uAo6vqFmHgEoJ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
