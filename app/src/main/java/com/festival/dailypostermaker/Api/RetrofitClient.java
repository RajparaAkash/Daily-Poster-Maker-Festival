package com.festival.dailypostermaker.Api;

import com.festival.dailypostermaker.MyUtils.MyUtil;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static volatile Retrofit retrofitInstance;

    private RetrofitClient() {
        // Private constructor to prevent instantiation
    }

    public static Retrofit getInstance() {
        if (retrofitInstance == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitInstance == null) {
                    retrofitInstance = new Retrofit.Builder()
                            .baseUrl(MyUtil.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofitInstance;
    }
}
