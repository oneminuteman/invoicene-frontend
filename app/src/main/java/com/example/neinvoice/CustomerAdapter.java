package com.example.neinvoice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private final List<Customer> customerList;
    private final OnInvoiceProcessListener listener;

    public interface OnInvoiceProcessListener {
        void onProcessInvoice(Customer customer);
    }

    public CustomerAdapter(List<Customer> customerList, OnInvoiceProcessListener listener) {
        this.customerList = customerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.customerName.setText(customer.getName());

        // Display Monthly Consumption Data
        if (customer.getMonthlyConsumptions() != null && !customer.getMonthlyConsumptions().isEmpty()) {
            StringBuilder consumptionDetails = new StringBuilder();
            for (Consumption consumption : customer.getMonthlyConsumptions()) {
                consumptionDetails.append(consumption.getMonth())
                        .append(" ")
                        .append(consumption.getYear())
                        .append(": ")
                        .append(consumption.getConsumption())
                        .append(" kWh\n");
            }
            holder.consumptionDetails.setText(consumptionDetails.toString().trim());
        } else {
            holder.consumptionDetails.setText(R.string.no_consumption_data);
        }

        // Handle "Process Invoice" Button Click
        holder.btnProcessInvoice.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProcessInvoice(customer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, consumptionDetails;
        Button btnProcessInvoice;

        CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tvCustomerName);
            consumptionDetails = itemView.findViewById(R.id.tvConsumptionDetails);
            btnProcessInvoice = itemView.findViewById(R.id.btnProcessInvoice);
        }
    }
}
