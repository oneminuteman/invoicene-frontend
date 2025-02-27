package com.example.neinvoice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up navigation buttons
        Button btnOjogiDrive = findViewById(R.id.btnOjogiDrive);
        Button btnPromise = findViewById(R.id.btnPromise);
        Button btnThorngroove = findViewById(R.id.btnThorngroove);
        Button btnVantage = findViewById(R.id.btnVantage);

        // Set click listeners for each button
        btnOjogiDrive.setOnClickListener(v -> openCustomerListActivity(9)); // Pass location ID
        btnPromise.setOnClickListener(v -> openCustomerListActivity(8));
        btnThorngroove.setOnClickListener(v -> openCustomerListActivity(7));
        btnVantage.setOnClickListener(v -> openCustomerListActivity(6));
    }

    private void openCustomerListActivity(int locationId) {
        Intent intent = new Intent(MainActivity.this, CustomerListActivity.class);
        intent.putExtra("LOCATION_ID", locationId);
        startActivity(intent);
    }
}
