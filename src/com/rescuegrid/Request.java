package com.rescuegrid;

public abstract class Request implements Urgent {
    private int id;
    private String location;
    private double latitude;
    private double longitude;
    private int people;
    private boolean verified = false;
    private String status = "PENDING";
    private String sourceChannel = "CONSOLE"; // CONSOLE, SMS, MOBILE_APP
    private String reporterPhone = "N/A";

    public Request(int id, String location, int people) {
        this(id, location, getDefaultLat(location), getDefaultLng(location), people, "CONSOLE", "N/A");
    }

    public Request(int id, String location, double latitude, double longitude, int people, String sourceChannel, String reporterPhone) {
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.people = people;
        this.sourceChannel = sourceChannel;
        this.reporterPhone = reporterPhone;
    }

    // Default Pune / Pimpri-Chinchwad GPS coordinates lookup for convenience
    public static double getDefaultLat(String loc) {
        if (loc == null) return 18.5204;
        String l = loc.toLowerCase();
        if (l.contains("kothrud")) return 18.5074;
        if (l.contains("baner")) return 18.5590;
        if (l.contains("pimpri") || l.contains("chinchwad")) return 18.6298;
        if (l.contains("sinhagad")) return 18.4698;
        if (l.contains("aundh")) return 18.5580;
        if (l.contains("hadapsar")) return 18.5089;
        if (l.contains("wakad")) return 18.5987;
        return 18.5204;
    }

    public static double getDefaultLng(String loc) {
        if (loc == null) return 73.8567;
        String l = loc.toLowerCase();
        if (l.contains("kothrud")) return 73.8077;
        if (l.contains("baner")) return 73.7868;
        if (l.contains("pimpri") || l.contains("chinchwad")) return 73.7997;
        if (l.contains("sinhagad")) return 73.8223;
        if (l.contains("aundh")) return 73.8075;
        if (l.contains("hadapsar")) return 73.9259;
        if (l.contains("wakad")) return 73.7644;
        return 73.8567;
    }

    public int getId() { return id; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getPeople() { return people; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSourceChannel() { return sourceChannel; }
    public void setSourceChannel(String sourceChannel) { this.sourceChannel = sourceChannel; }
    public String getReporterPhone() { return reporterPhone; }
    public void setReporterPhone(String reporterPhone) { this.reporterPhone = reporterPhone; }

    public abstract String getType();
    public abstract String getExtra();

    @Override
    public int getUrgency() {
        int score = people * 5;
        if (verified) {
            score = score + 10;
        }
        return score;
    }

    @Override
    public String getUrgencyLevel() {
        if ("RESOLVED".equalsIgnoreCase(status) || "SAFE".equalsIgnoreCase(status)) {
            return "RESOLVED";
        }
        int u = getUrgency();
        if (u >= 70) return "CRITICAL";
        if (u >= 50) return "HIGH";
        if (u >= 30) return "MEDIUM";
        return "LOW";
    }

    @Override
    public String getUrgencyHexColor() {
        String level = getUrgencyLevel();
        switch (level) {
            case "CRITICAL": return "#D62828"; // Alert Red
            case "HIGH":     return "#F77F00"; // High Orange
            case "MEDIUM":   return "#FCBF49"; // Medium Amber
            case "RESOLVED": return "#2E9E5B"; // Safe Green
            default:         return "#328CC1"; // Low Flood Blue
        }
    }

    @Override
    public String toString() {
        return "#" + id + " [" + getType() + " | " + sourceChannel + "] " + location
                + String.format(" (GPS: %.4f, %.4f)", latitude, longitude)
                + " | People: " + people
                + " | Urgency: " + getUrgency() + " (" + getUrgencyLevel() + ")"
                + " | Verified: " + verified + " | " + status;
    }
}
