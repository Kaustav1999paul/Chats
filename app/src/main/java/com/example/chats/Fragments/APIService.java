package com.example.chats.Fragments;

import com.example.chats.Notifications.MyResponse;
import com.example.chats.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAdbAZvac:APA91bHHunLdWeh8ld3OUysYo4K4O01Mevru9TIT6jqEq71cPeCAk8ltxcTCfS21gMU0TM-AS4wWf7M8mcI7ziC4ZqVpFhutP3rdUNFRlOG4gEX-eqMNoEbZqgC0uw5B-CXAZLCqZgtk"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
