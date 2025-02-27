package com.example.neinvoice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    // Fetch customers for a specific location
    @GET("locations/{locationId}/customers")
    Call<List<Customer>> getCustomersByLocation(@Path("locationId") int locationId);

    // Fetch monthly consumptions for a specific customer in a specific location
    @GET("locations/{locationId}/customers/{customerId}/monthly_consumptions")
    Call<List<Consumption>> getCustomerConsumptions(
            @Path("locationId") int locationId,
            @Path("customerId") int customerId
    );
}
