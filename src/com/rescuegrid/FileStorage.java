package com.rescuegrid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileStorage {
    private static final String FILE_NAME = "data/requests.txt";

    // Writes every request as one line of text (including GPS coordinates and channel)
    public static void save(ArrayList<Request> requests) {
        new File("data").mkdir();
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME));
            for (Request r : requests) {
                writer.println(r.getType() + "," + r.getId() + "," + r.getLocation() + ","
                        + r.getPeople() + "," + r.isVerified() + "," + r.getStatus() + ","
                        + r.getExtra() + "," + r.getLatitude() + "," + r.getLongitude() + ","
                        + r.getSourceChannel() + "," + r.getReporterPhone());
            }
            writer.close();
            System.out.println("Data saved to file.");
        } catch (Exception e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    // Reads the file line by line and rebuilds the request objects (backward-compatible)
    public static ArrayList<Request> load() {
        ArrayList<Request> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return list;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                int id = Integer.parseInt(parts[1]);
                String location = parts[2];
                int people = Integer.parseInt(parts[3]);
                boolean verified = Boolean.parseBoolean(parts[4]);
                String status = parts[5];

                double lat = parts.length > 7 ? Double.parseDouble(parts[7]) : Request.getDefaultLat(location);
                double lng = parts.length > 8 ? Double.parseDouble(parts[8]) : Request.getDefaultLng(location);
                String channel = parts.length > 9 ? parts[9] : "CONSOLE";
                String phone = parts.length > 10 ? parts[10] : "N/A";

                Request r = null;
                if (type.equals("FLOOD")) {
                    r = new FloodRequest(id, location, lat, lng, people, Integer.parseInt(parts[6]), channel, phone);
                } else if (type.equals("MEDICAL")) {
                    r = new MedicalRequest(id, location, lat, lng, people, Boolean.parseBoolean(parts[6]), channel, phone);
                }

                if (r != null) {
                    r.setVerified(verified);
                    r.setStatus(status);
                    list.add(r);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Could not load: " + e.getMessage());
        }
        return list;
    }
}
