package com.example.neinvoice;

import android.content.Context;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://10.0.2.2:3001/";

    public static ApiService getApiService(Context context) {
        // Get an OkHttpClient that supports SSL
        OkHttpClient client = SSLHelper.getSafeOkHttpClient(context);

        // Create Retrofit instance using the SSL-configured OkHttpClient
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}

