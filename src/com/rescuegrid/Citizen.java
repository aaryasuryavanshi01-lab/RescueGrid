package com.rescuegrid;

public class Citizen extends Person {
    public Citizen(String name, String phone, String location, double latitude, double longitude) {
        super(name, phone, location, latitude, longitude);
    }

    @Override
    public String getRole() { return "Citizen"; }

    // Format an SMS distress string for offline/low-network submission
    public String composeDistressSms(String type, int people, String extraInfo) {
        return String.format("SOS|%s|%s|%.4f,%.4f|%d|%s|%s",
                type.toUpperCase(), getLocation(), getLatitude(), getLongitude(),
                people, extraInfo, getPhone());
    }
}
