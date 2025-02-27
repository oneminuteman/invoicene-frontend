package com.example.neinvoice;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Customer {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("location_id")
    private int locationId;

    @SerializedName("monthly_consumptions") // Maps to backend field
    private List<Consumption> monthlyConsumptions;

    public Customer() {
    }

    public Customer(int id, String name, int locationId, List<Consumption> monthlyConsumptions) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
        this.monthlyConsumptions = monthlyConsumptions;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public List<Consumption> getMonthlyConsumptions() {
        return monthlyConsumptions;
    }

    public void setMonthlyConsumptions(List<Consumption> monthlyConsumptions) {
        this.monthlyConsumptions = monthlyConsumptions;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locationId=" + locationId +
                ", monthlyConsumptions=" + monthlyConsumptions +
                '}';
    }
}

