package com.example.swiftdelivery.admin;

public class AdminManageAgent {

    private String name;
    private String email;
    private String phone;
    private String resident;
    private String vehicle;
    private String registration;

    public AdminManageAgent() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResident() {
        return resident;
    }

    public void setResident(String resident) {
        this.resident = resident;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public AdminManageAgent(String name, String email, String phone, String resident, String vehicle, String registration) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.resident = resident;
        this.vehicle = vehicle;
        this.registration = registration;
    }
}