package com.example.neinvoice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("customers")
    Call<List<Customer>> getCustomers();

    @GET("consumptions")
    Call<List<Consumption>> getConsumptions();
}
