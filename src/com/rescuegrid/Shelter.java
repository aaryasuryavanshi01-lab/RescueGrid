package com.rescuegrid;

public class Shelter {
    private String name;
    private String location;
    private double latitude;
    private double longitude;
    private int capacity;
    private int occupied = 0;

    public Shelter(String name, String location, int capacity) {
        this(name, location, Request.getDefaultLat(location), Request.getDefaultLng(location), capacity);
    }

    public Shelter(String name, String location, double latitude, double longitude, int capacity) {
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
    }

    public boolean admit(int count) {
        if (occupied + count > capacity) {
            return false;
        }
        occupied = occupied + count;
        return true;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getCapacity() { return capacity; }
    public int getOccupied() { return occupied; }
    public int getAvailable() { return capacity - occupied; }

    @Override
    public String toString() {
        return name + " (" + location + String.format(" @ %.4f, %.4f", latitude, longitude) + ") - "
                + getAvailable() + " of " + capacity + " beds free";
    }
}
