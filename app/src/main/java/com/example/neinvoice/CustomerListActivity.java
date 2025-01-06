package com.example.neinvoice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        recyclerView = findViewById(R.id.recyclerViewCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchCustomers();
    }

    private void fetchCustomers() {
        ApiService apiService = RetrofitInstance.getRetrofit().create(ApiService.class);

        apiService.getCustomers().enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customers = response.body();
                    customerAdapter = new CustomerAdapter(customers);
                    recyclerView.setAdapter(customerAdapter);
                } else {
                    Log.e("CustomerListActivity", "Failed to fetch customers: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Log.e("CustomerListActivity", "Error fetching customers", t);
            }
        });
    }
}



