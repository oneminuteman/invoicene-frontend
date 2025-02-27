package com.example.neinvoice;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.io.IOException;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromiseActivity extends AppCompatActivity {

    private LinearLayout invoicesLayout;
    private final int locationId = 6; // Set dynamically if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise);

        // Assuming your layout now has a button to fetch customers
        // If using the updated layout with btnFetchCustomers, adjust accordingly.
        invoicesLayout = findViewById(R.id.invoicesLayout);
        Button btnFetchCustomers = findViewById(R.id.btnFetchCustomers);

        // When btnFetchCustomers is clicked, fetch customer names
        btnFetchCustomers.setOnClickListener(v -> fetchCustomers(PromiseActivity.this, locationId));
    }

    // Fetch only customer names for the given location
    private void fetchCustomers(Context context, int locationId) {
        ApiService apiService = ApiClient.getApiService(this);

        apiService.getCustomersByLocation(locationId).enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customers = response.body();
                    invoicesLayout.removeAllViews(); // Clear previous views

                    for (Customer customer : customers) {
                        displayCustomerUI(context, customer);
                    }
                } else {
                    Log.e("PromiseActivity", "Failed to fetch customers: " + response.message());
                    Toast.makeText(context, "Failed to load customers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {
                Log.e("PromiseActivity", "Error fetching customers", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // For each customer, display their name with a "Process Invoice" button
    private void displayCustomerUI(Context context, Customer customer) {
        LinearLayout customerLayout = new LinearLayout(context);
        customerLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView customerNameText = new TextView(context);
        customerNameText.setText(customer.getName());
        customerNameText.setPadding(20, 10, 20, 10);

        Button processInvoiceButton = new Button(context);
        processInvoiceButton.setText("Process Invoice");
        processInvoiceButton.setOnClickListener(v -> fetchConsumptionData(context, customer));

        customerLayout.addView(customerNameText);
        customerLayout.addView(processInvoiceButton);
        invoicesLayout.addView(customerLayout);
    }

    // Fetch consumption data for a given customer (only last two months)
    private void fetchConsumptionData(Context context, Customer customer) {
        ApiService apiService = ApiClient.getApiService(context);

        // Determine the current and previous month names and year
        Calendar calendar = Calendar.getInstance();
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        String currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.add(Calendar.MONTH, -1);
        String previousMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, locale);

        apiService.getCustomerConsumptions(customer.getLocationId(), customer.getId()).enqueue(new Callback<List<Consumption>>() {
            @Override
            public void onResponse(@NonNull Call<List<Consumption>> call, @NonNull Response<List<Consumption>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double previousConsumption = 0, currentConsumption = 0;

                    for (Consumption consumption : response.body()) {
                        if (consumption.getMonth().equalsIgnoreCase(previousMonth) && consumption.getYear() == currentYear) {
                            previousConsumption = consumption.getConsumption();
                        }
                        if (consumption.getMonth().equalsIgnoreCase(currentMonth) && consumption.getYear() == currentYear) {
                            currentConsumption = consumption.getConsumption();
                        }
                    }

                    // Calculate invoice details and generate PDF
                    processInvoiceForCustomer(context, customer, previousConsumption, currentConsumption);
                } else {
                    Log.e("PromiseActivity", "Failed to fetch consumption data: " + response.message());
                    Toast.makeText(context, "Failed to load consumption data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Consumption>> call, @NonNull Throwable t) {
                Log.e("PromiseActivity", "Error fetching consumption data", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Process invoice calculation and generate PDF for the customer
    private void processInvoiceForCustomer(Context context, Customer customer, double previousConsumption, double currentConsumption) {
        double monthlyConsumption = currentConsumption - previousConsumption;
        double cost = monthlyConsumption * 150;
        double totalCharge = cost + 200;

        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Paint paint = new Paint();
            int yPosition = 50;
            page.getCanvas().drawText(customer.getName(), 50, yPosition, paint);
            yPosition += 50;
            page.getCanvas().drawText("INVOICE", 50, yPosition, paint);
            yPosition += 50;
            page.getCanvas().drawText("Consumption: " + monthlyConsumption + " units", 50, yPosition, paint);
            yPosition += 50;
            page.getCanvas().drawText("Cost: Ksh " + cost, 50, yPosition, paint);
            yPosition += 50;
            page.getCanvas().drawText("Standing Charge: Ksh 200", 50, yPosition, paint);
            yPosition += 50;
            page.getCanvas().drawText("Final Amount: Ksh " + totalCharge, 50, yPosition, paint);

            pdfDocument.finishPage(page);

            // Save PDF in Documents/Invoices directory
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Invoices");
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
            }
            File file = new File(directory, customer.getName() + "_Invoice.pdf");
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();

            Log.d("PromiseActivity", "Invoice saved at: " + file.getAbsolutePath());
            Toast.makeText(context, "Invoice generated for " + customer.getName(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("PromiseActivity", "Error generating invoice", e);
            Toast.makeText(context, "Error generating invoice", Toast.LENGTH_SHORT).show();
        }
    }
}

