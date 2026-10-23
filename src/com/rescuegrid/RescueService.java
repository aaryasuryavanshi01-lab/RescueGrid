package com.rescuegrid;

import java.util.ArrayList;

public class RescueService {
    private ArrayList<Request> requests = new ArrayList<>();
    private ArrayList<Shelter> shelters = new ArrayList<>();
    private ArrayList<Volunteer> volunteers = new ArrayList<>();

    public void addRequest(Request r) { requests.add(r); }
    public void addShelter(Shelter s) { shelters.add(s); }
    public void addVolunteer(Volunteer v) { volunteers.add(v); }

    public ArrayList<Request> getRequests() { return requests; }
    public ArrayList<Shelter> getShelters() { return shelters; }
    public ArrayList<Volunteer> getVolunteers() { return volunteers; }

    // Sorts a copy of the list so the most urgent request comes first
    public ArrayList<Request> getSortedByUrgency() {
        ArrayList<Request> sorted = new ArrayList<>(requests);
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = 0; j < sorted.size() - i - 1; j++) {
                if (sorted.get(j).getUrgency() < sorted.get(j + 1).getUrgency()) {
                    Request temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                }
            }
        }
        return sorted;
    }

    // Returns only verified incidents for the Live GPS Incident Map
    public ArrayList<Request> getVerifiedIncidentsForMap() {
        ArrayList<Request> verifiedList = new ArrayList<>();
        for (Request r : getSortedByUrgency()) {
            if (r.isVerified()) {
                verifiedList.add(r);
            }
        }
        return verifiedList;
    }

    public boolean verifyRequest(int id) {
        for (Request r : requests) {
            if (r.getId() == id) {
                r.setVerified(true);
                return true;
            }
        }
        return false;
    }

    // Haversine formula to compute GPS distance in kilometers
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Alerts nearby available volunteers within radiusKm (or matching location name)
    public ArrayList<Volunteer> alertNearbyVolunteers(int requestId, double radiusKm) {
        ArrayList<Volunteer> alerted = new ArrayList<>();
        Request target = null;
        for (Request r : requests) {
            if (r.getId() == requestId) {
                target = r;
                break;
            }
        }
        if (target == null) return alerted;

        for (Volunteer v : volunteers) {
            if (!v.isAvailable()) continue;
            double dist = calculateDistanceKm(target.getLatitude(), target.getLongitude(), v.getLatitude(), v.getLongitude());
            boolean sameArea = v.getLocation().equalsIgnoreCase(target.getLocation());
            if (dist <= radiusKm || sameArea) {
                String msg = String.format("[ALERT %s] Request #%d (%s) at %s (%.2f km away) | People: %d | Urgency: %d (%s)",
                        target.getUrgencyLevel(), target.getId(), target.getType(),
                        target.getLocation(), dist, target.getPeople(), target.getUrgency(), target.getUrgencyLevel());
                v.receiveAlert(msg);
                alerted.add(v);
            }
        }
        return alerted;
    }

    // Overloading: same name, different parameters
    public ArrayList<Request> find(String location) {
        ArrayList<Request> result = new ArrayList<>();
        for (Request r : requests) {
            if (r.getLocation().equalsIgnoreCase(location)) {
                result.add(r);
            }
        }
        return result;
    }

    public ArrayList<Request> find(int minUrgency) {
        ArrayList<Request> result = new ArrayList<>();
        for (Request r : requests) {
            if (r.getUrgency() >= minUrgency) {
                result.add(r);
            }
        }
        return result;
    }

    public boolean assignVolunteer(int requestId) {
        for (Request r : requests) {
            if (r.getId() == requestId) {
                Volunteer nearest = null;
                double minDist = Double.MAX_VALUE;
                for (Volunteer v : volunteers) {
                    if (v.isAvailable()) {
                        double d = calculateDistanceKm(r.getLatitude(), r.getLongitude(), v.getLatitude(), v.getLongitude());
                        if (d < minDist) {
                            minDist = d;
                            nearest = v;
                        }
                    }
                }
                if (nearest != null) {
                    nearest.setAvailable(false);
                    r.setStatus("ASSIGNED (" + nearest.getName() + ")");
                    System.out.println("Assigned nearest volunteer " + nearest.getName()
                            + String.format(" (%.2f km away)", minDist) + " to request #" + requestId);
                    return true;
                }
            }
        }
        return false;
    }
}
