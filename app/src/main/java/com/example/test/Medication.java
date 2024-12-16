package com.example.test;
public class Medication {
    private String name;
    private String type;
    private String time;
    private String frequency;
    private int quantity;

    // Default constructor required for Firebase
    public Medication() {
    }

    // Constructor
    public Medication(String frequency,String name, int quantity,  String time,String type) {
        this.name = name;
        this.type = type;
        this.time = time;
        this.frequency = frequency;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

