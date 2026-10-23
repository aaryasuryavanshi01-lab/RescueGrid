package com.rescuegrid;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static RescueService service = new RescueService();
    static int nextId = 1;

    public static void main(String[] args) {
        for (Request r : FileStorage.load()) {
            service.addRequest(r);
            if (r.getId() >= nextId) {
                nextId = r.getId() + 1;
            }
        }

        // Seed sample shelters, volunteers with GPS coordinates, and sample verified requests
        service.addShelter(new Shelter("Govt School", "Kothrud", 18.5074, 73.8077, 100));
        service.addShelter(new Shelter("Community Hall", "Baner", 18.5590, 73.7868, 50));
        service.addShelter(new Shelter("PCMC Relief Camp", "Pimpri", 18.6298, 73.7997, 120));

        service.addVolunteer(new Volunteer("Amit", "9876543210", "First Aid", "Kothrud", 18.5085, 73.8090));
        service.addVolunteer(new Volunteer("Priya", "9123456780", "Boat Rescue", "Baner", 18.5602, 73.7880));
        service.addVolunteer(new Volunteer("Rohan", "9822112233", "Paramedic", "Pimpri", 18.6280, 73.8010));

        if (service.getRequests().isEmpty()) {
            FloodRequest f1 = new FloodRequest(nextId++, "Kothrud", 18.5074, 73.8077, 6, 5, "MOBILE_APP", "9890112233");
            f1.setVerified(true);
            service.addRequest(f1);

            MedicalRequest m1 = new MedicalRequest(nextId++, "Baner", 18.5590, 73.7868, 3, true, "SMS", "9765432109");
            m1.setVerified(true);
            service.addRequest(m1);
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            runAutomatedDemo();
            return;
        }

        int choice = -1;
        while (choice != 0) {
            System.out.println("\n===== RescueGrid (Flood SOS Edition) =====");
            System.out.println("1. Add flood request (with GPS)");
            System.out.println("2. Add medical request (with GPS)");
            System.out.println("3. View requests (most urgent first)");
            System.out.println("4. Verify a request");
            System.out.println("5. View shelters");
            System.out.println("6. Admit people to shelter");
            System.out.println("7. View volunteers & alert inbox");
            System.out.println("8. Assign nearest volunteer to request");
            System.out.println("9. Search requests by location");
            System.out.println("10. Search requests by minimum urgency");
            System.out.println("11. [NEW] View Live GPS Map of Verified Incidents");
            System.out.println("12. [NEW] Citizen SMS / Mobile App Distress Submission");
            System.out.println("13. [NEW] Alert Nearby Volunteers by Location / GPS Radius");
            System.out.println("0. Save and exit");
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1: addFlood(); break;
                case 2: addMedical(); break;
                case 3: printList(service.getSortedByUrgency()); break;
                case 4: verify(); break;
                case 5: viewShelters(); break;
                case 6: admit(); break;
                case 7: viewVolunteers(); break;
                case 8: assign(); break;
                case 9: printList(service.find(readText("Location: "))); break;
                case 10: printList(service.find(readInt("Minimum urgency: "))); break;
                case 11: viewLiveGpsMap(); break;
                case 12: submitCitizenDistress(); break;
                case 13: triggerNearbyVolunteerAlerts(); break;
                case 0: FileStorage.save(service.getRequests()); System.out.println("Stay safe!"); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void runAutomatedDemo() {
        System.out.println("=== Running RescueGrid Automated Verification Demo ===");
        Request smsReq = SmsDistressGateway.parseDistressSms(nextId++, "SOS|FLOOD|Pimpri|18.6298,73.7997|8|6|9850011223");
        smsReq.setVerified(true);
        service.addRequest(smsReq);
        System.out.println("Parsed Citizen SMS Distress: " + smsReq);

        viewLiveGpsMap();

        ArrayList<Volunteer> alerted = service.alertNearbyVolunteers(smsReq.getId(), 5.0);
        System.out.println("Alerted " + alerted.size() + " nearby volunteer(s) within 5.0 km of " + smsReq.getLocation());
        for (Volunteer v : alerted) {
            System.out.println(" -> " + v.getName() + " Inbox: " + v.getAlertInbox().get(v.getAlertInbox().size() - 1));
        }
        FileStorage.save(service.getRequests());
    }

    static void addFlood() {
        String location = readText("Location: ");
        double lat = readDouble("Latitude (or Enter for default): ", Request.getDefaultLat(location));
        double lng = readDouble("Longitude (or Enter for default): ", Request.getDefaultLng(location));
        int people = readInt("Number of people: ");
        int level = readInt("Water level (feet): ");
        service.addRequest(new FloodRequest(nextId, location, lat, lng, people, level, "CONSOLE", "N/A"));
        System.out.println("Request #" + nextId + " added.");
        nextId++;
    }

    static void addMedical() {
        String location = readText("Location: ");
        double lat = readDouble("Latitude (or Enter for default): ", Request.getDefaultLat(location));
        double lng = readDouble("Longitude (or Enter for default): ", Request.getDefaultLng(location));
        int people = readInt("Number of people: ");
        String answer = readText("Is anyone critical? (y/n): ");
        boolean critical = answer.equalsIgnoreCase("y");
        service.addRequest(new MedicalRequest(nextId, location, lat, lng, people, critical, "CONSOLE", "N/A"));
        System.out.println("Request #" + nextId + " added.");
        nextId++;
    }

    static void viewLiveGpsMap() {
        System.out.println("\n--- LIVE MAP OF VERIFIED INCIDENTS (GPS COORDINATES) ---");
        ArrayList<Request> verified = service.getVerifiedIncidentsForMap();
        if (verified.isEmpty()) {
            System.out.println("No verified incidents on the live map yet.");
            return;
        }
        for (Request r : verified) {
            System.out.printf("PIN #%d [%s | %s | Color: %s] -> %s (Lat: %.4f, Lng: %.4f) | People: %d | Status: %s%n",
                    r.getId(), r.getType(), r.getUrgencyLevel(), r.getUrgencyHexColor(),
                    r.getLocation(), r.getLatitude(), r.getLongitude(), r.getPeople(), r.getStatus());
        }
    }

    static void submitCitizenDistress() {
        System.out.println("1. Simulate incoming Citizen SMS string (SOS|TYPE|LOC|LAT,LNG|PEOPLE|EXTRA|PHONE)");
        System.out.println("2. Submit via Citizen Mobile App form");
        int mode = readInt("Select mode (1 or 2): ");
        try {
            if (mode == 1) {
                String raw = readText("Paste SMS payload: ");
                Request r = SmsDistressGateway.parseDistressSms(nextId, raw);
                service.addRequest(r);
                System.out.println("SMS Distress Request #" + nextId + " registered: " + r);
                nextId++;
            } else {
                String phone = readText("Citizen Phone: ");
                String type = readText("Type (FLOOD/MEDICAL): ");
                String loc = readText("Location: ");
                double lat = readDouble("GPS Latitude (Enter for auto): ", Request.getDefaultLat(loc));
                double lng = readDouble("GPS Longitude (Enter for auto): ", Request.getDefaultLng(loc));
                int people = readInt("People stranded: ");
                String extra = readText(type.equalsIgnoreCase("FLOOD") ? "Water level (feet): " : "Critical? (true/false): ");
                Request r = SmsDistressGateway.createMobileAppDistress(nextId, type, loc, lat, lng, people, extra, phone);
                service.addRequest(r);
                System.out.println("Mobile App Distress Request #" + nextId + " registered: " + r);
                nextId++;
            }
        } catch (Exception e) {
            System.out.println("Failed to process distress request: " + e.getMessage());
        }
    }

    static void triggerNearbyVolunteerAlerts() {
        int id = readInt("Request ID to broadcast alert for: ");
        double radius = readDouble("Search radius in km (Enter for 5.0 km): ", 5.0);
        ArrayList<Volunteer> alerted = service.alertNearbyVolunteers(id, radius);
        if (alerted.isEmpty()) {
            System.out.println("No available volunteers found within " + radius + " km.");
        } else {
            System.out.println("Dispatched location-based alerts to " + alerted.size() + " volunteer(s):");
            for (Volunteer v : alerted) {
                System.out.println(" - " + v.getName() + " (" + v.getLocation() + ", " + v.getPhone() + ")");
            }
        }
    }

    static void verify() {
        int id = readInt("Request id to verify: ");
        if (service.verifyRequest(id)) {
            System.out.println("Request verified and pinned to Live GPS Map.");
        } else {
            System.out.println("Request not found.");
        }
    }

    static void viewShelters() {
        ArrayList<Shelter> list = service.getShelters();
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i));
        }
    }

    static void admit() {
        viewShelters();
        int number = readInt("Shelter number: ");
        if (number < 1 || number > service.getShelters().size()) {
            System.out.println("Invalid shelter.");
            return;
        }
        int count = readInt("How many people: ");
        if (service.getShelters().get(number - 1).admit(count)) {
            System.out.println("Admitted successfully.");
        } else {
            System.out.println("Not enough space in this shelter.");
        }
    }

    static void viewVolunteers() {
        for (Volunteer v : service.getVolunteers()) {
            System.out.println(v);
            for (String alert : v.getAlertInbox()) {
                System.out.println("   Inbox -> " + alert);
            }
        }
    }

    static void assign() {
        int id = readInt("Request id: ");
        if (!service.assignVolunteer(id)) {
            System.out.println("Could not assign (request not found or no volunteer free).");
        }
    }

    static void printList(ArrayList<Request> list) {
        if (list.isEmpty()) {
            System.out.println("No requests found.");
            return;
        }
        for (Request r : list) {
            System.out.println(r);
        }
    }

    static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    static double readDouble(String message, double defaultVal) {
        System.out.print(message);
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return defaultVal;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    static String readText(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }
}
