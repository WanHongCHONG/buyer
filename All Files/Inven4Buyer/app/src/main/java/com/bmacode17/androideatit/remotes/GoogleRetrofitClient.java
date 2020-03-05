package com.bmacode17.androideatit.remotes;


import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class GoogleRetrofitClient {
    private static Retrofit retrofit1 = null;
    public static Retrofit getGoogleClient(String baseUrl){

        if(retrofit1 == null){

            retrofit1 = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit1;
    }

}
