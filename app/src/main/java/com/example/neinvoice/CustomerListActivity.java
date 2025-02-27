package com.example.neinvoice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerListActivity extends AppCompatActivity implements CustomerAdapter.OnInvoiceProcessListener {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private int locationId;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        recyclerView = findViewById(R.id.recyclerViewCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get location ID and name from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOCATION_ID") && intent.hasExtra("LOCATION_NAME")) {
            int tempLocationId = intent.getIntExtra("LOCATION_ID", -1);

            // Ensure locationId is within allowed values (6, 7, 8, 9)
            if (tempLocationId == 6 || tempLocationId == 7 || tempLocationId == 8 || tempLocationId == 9) {
                locationId = tempLocationId;
                locationName = intent.getStringExtra("LOCATION_NAME");
            } else {
                Log.e("CustomerListActivity", "Invalid location ID: " + tempLocationId);
                Toast.makeText(this, "Invalid location selected", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        // Get customer list from intent
        if (intent.hasExtra("CUSTOMER_LIST")) {
            List<Customer> customerList = (List<Customer>) intent.getSerializableExtra("CUSTOMER_LIST");

            if (customerList != null) {
                customerAdapter = new CustomerAdapter(customerList, this); // Pass this as the listener
                recyclerView.setAdapter(customerAdapter);
            } else {
                Log.e("CustomerListActivity", "No customers received from intent");
                Toast.makeText(this, "No customers available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onProcessInvoice(Customer customer) {
        // Handle invoice processing here
        Toast.makeText(this, "Processing invoice for " + customer.getName(), Toast.LENGTH_SHORT).show();
    }
}
