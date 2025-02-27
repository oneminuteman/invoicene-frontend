package com.example.neinvoice;

import com.google.gson.annotations.SerializedName;

public class Consumption {
    private int id;

    @SerializedName("customer_id") // Matches backend field
    private int customerId;

    private String month;
    private int year;
    private double consumption;

    // Constructor
    public Consumption(int id, int customerId, String month, int year, double consumption) {
        this.id = id;
        this.customerId = customerId;
        this.month = month;
        this.year = year;
        this.consumption = consumption;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }
}


