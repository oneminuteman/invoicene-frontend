package com.example.neinvoice;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://192.168.100.3:3001/";
    private static Retrofit retrofit;

    // Singleton instance of Retrofit
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Convenience method to create an API Service
    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }
}
