package com.example.neinvoice;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VantageActivity extends AppCompatActivity {

    private LinearLayout invoicesLayout;
    private final int locationId = 6; // Set dynamically if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vantage);

        invoicesLayout = findViewById(R.id.invoicesLayout);
        Button btnFetchCustomers = findViewById(R.id.btnFetchCustomers);

        btnFetchCustomers.setOnClickListener(v -> fetchCustomers(VantageActivity.this, locationId));
    }

    private void fetchCustomers(Context context, int locationId) {
        ApiService apiService = ApiClient.getApiService(this);

        apiService.getCustomersByLocation(locationId).enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customers = response.body();
                    invoicesLayout.removeAllViews();

                    for (Customer customer : customers) {
                        displayCustomerUI(context, customer);
                    }
                } else {
                    Log.e("VantageActivity", "Failed to fetch customers: " + response.message());
                    Toast.makeText(context, "Failed to load customers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {
                Log.e("VantageActivity", "Error fetching customers", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private void fetchConsumptionData(Context context, Customer customer) {
        ApiService apiService = ApiClient.getApiService(context);

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

                    processInvoice(context, customer, previousConsumption, currentConsumption);
                } else {
                    Log.e("VantageActivity", "Failed to fetch consumption data: " + response.message());
                    Toast.makeText(context, "Failed to load consumption data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Consumption>> call, @NonNull Throwable t) {
                Log.e("VantageActivity", "Error fetching consumption data", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processInvoice(Context context, Customer customer, double previousConsumption, double currentConsumption) {
        double monthlyConsumption = currentConsumption - previousConsumption;
        double cost = monthlyConsumption * 150;
        double totalCharge = cost + 200;

        String filePath = generateInvoice(customer.getName(), monthlyConsumption, cost, totalCharge);
        if (filePath != null) {
            File file = new File(filePath);

            if (file.exists()) {
                openInvoice(file);
            } else {
                Toast.makeText(this, "Invoice not found!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Failed to generate invoice!", Toast.LENGTH_LONG).show();
        }
    }

    private String generateInvoice(String customerName, double monthlyConsumption, double cost, double totalCharge) {
        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Paint paint = new Paint();
            int yPosition = 50;
            page.getCanvas().drawText(customerName, 50, yPosition, paint);
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

            File directory = new File(getExternalFilesDir("Invoices"), "");
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
            }
            File file = new File(directory, customerName + "_Invoice.pdf");
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();

            Log.d("VantageActivity", "Invoice saved at: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("VantageActivity", "Error generating invoice", e);
            return null;
        }
    }

    private void openInvoice(File file) {
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer installed!", Toast.LENGTH_SHORT).show();
        }
    }
}
