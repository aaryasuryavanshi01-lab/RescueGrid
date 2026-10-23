package com.rescuegrid;

public class Person {
    private String name;
    private String phone;
    private String location;
    private double latitude;
    private double longitude;

    public Person(String name, String phone) {
        this(name, phone, "Pune Central", 18.5204, 73.8567);
    }

    public Person(String name, String phone, String location, double latitude, double longitude) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public void setLocation(String location, double latitude, double longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getRole() { return "Person"; }
}
