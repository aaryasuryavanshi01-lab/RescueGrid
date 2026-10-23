package com.rescuegrid;

import java.util.ArrayList;

public class Volunteer extends Person {
    private String skill;
    private boolean available = true;
    private ArrayList<String> alertInbox = new ArrayList<>();

    public Volunteer(String name, String phone, String skill) {
        super(name, phone, "Kothrud", 18.5074, 73.8077);
        this.skill = skill;
    }

    public Volunteer(String name, String phone, String skill, String location, double latitude, double longitude) {
        super(name, phone, location, latitude, longitude);
        this.skill = skill;
    }

    public String getSkill() { return skill; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public void receiveAlert(String alertMessage) {
        alertInbox.add(alertMessage);
    }

    public ArrayList<String> getAlertInbox() { return alertInbox; }

    @Override
    public String getRole() { return "Volunteer"; }

    @Override
    public String toString() {
        return getRole() + ": " + getName() + " (" + getPhone() + ") | Location: " + getLocation()
                + String.format(" [%.4f, %.4f]", getLatitude(), getLongitude())
                + " | Skill: " + skill + " | Available: " + available
                + " | Alerts: " + alertInbox.size();
    }
}
