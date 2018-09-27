package com.example.lqs2.courseapp.interfaces;


import com.example.lqs2.courseapp.reception.SendTweetReception;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface TweetInterface {

    @POST("/tweet/nt")
    Call<SendTweetReception> sendSweet(@Url() String url, @Body() RequestBody body);

}
