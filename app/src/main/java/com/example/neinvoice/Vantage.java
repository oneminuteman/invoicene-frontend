package com.example.neinvoice;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Vantage {

    public List<Customer> fetchCustomers() {
        // Fetch customers tied to the Vantage location (API call implementation)
        return new ArrayList<>(); // Replace with actual fetched data
    }

    public double fetchConsumption(int customerId, String month, int year) {
        // Fetch consumption data for a specific customer and month/year (API call implementation)
        return 0.0; // Replace with actual fetched data
    }

    public void generateInvoice(Context context, Customer customer, double consumption1, double consumption2) {
        double monthlyConsumption = consumption2 - consumption1;
        double cost = monthlyConsumption * 150;
        double totalCharge = cost + 200;

        // Generate a PDF
        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            // Add content to the page
            page.getCanvas().drawText("Customer Invoice", 50, 50, null);
            page.getCanvas().drawText("Name: " + customer.getName(), 50, 100, null);
            page.getCanvas().drawText("Monthly Consumption: " + monthlyConsumption, 50, 150, null);
            page.getCanvas().drawText("Cost (150 KES/unit): " + cost, 50, 200, null);
            page.getCanvas().drawText("Standing Charge: 200", 50, 250, null);
            page.getCanvas().drawText("Final Charge: " + totalCharge, 50, 300, null);

            pdfDocument.finishPage(page);

            // Save PDF to device
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Invoices");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, customer.getName() + "_Invoice.pdf");
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processInvoices(Context context) {
        List<Customer> customers = fetchCustomers();
        for (Customer customer : customers) {
            double previousConsumption = fetchConsumption(customer.getId(), "November", 2024); // Example
            double currentConsumption = fetchConsumption(customer.getId(), "December", 2024); // Example
            generateInvoice(context, customer, previousConsumption, currentConsumption);
        }
    }
}



