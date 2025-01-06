package com.example.neinvoice;

import com.google.gson.annotations.SerializedName;

public class Customer {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("location_id")
    private int locationId;

    // Default constructor
    public Customer() {
    }

    // Parameterized constructor for convenience
    public Customer(int id, String name, int locationId) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
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

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locationId=" + locationId +
                '}';
    }
}
