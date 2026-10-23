package com.rescuegrid;

/**
 * Simulates an SMS & Mobile App Gateway that parses citizen distress payloads
 * into strongly-typed Request objects (FloodRequest or MedicalRequest).
 *
 * Supported SMS syntax:
 *   SOS|FLOOD|<Location>|<Lat>,<Lng>|<People>|<WaterLevelFt>|<Phone>
 *   SOS|MEDICAL|<Location>|<Lat>,<Lng>|<People>|<Critical:true/false>|<Phone>
 */
public class SmsDistressGateway {

    public static Request parseDistressSms(int nextId, String rawSms) {
        if (rawSms == null || !rawSms.toUpperCase().startsWith("SOS|")) {
            throw new IllegalArgumentException("Invalid SMS format. Expected: SOS|TYPE|LOCATION|LAT,LNG|PEOPLE|EXTRA|PHONE");
        }
        String[] parts = rawSms.split("\\|");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Incomplete SMS fields.");
        }
        String type = parts[1].trim().toUpperCase();
        String location = parts[2].trim();
        String[] coords = parts[3].trim().split(",");
        double lat = coords.length == 2 ? Double.parseDouble(coords[0].trim()) : Request.getDefaultLat(location);
        double lng = coords.length == 2 ? Double.parseDouble(coords[1].trim()) : Request.getDefaultLng(location);
        int people = Integer.parseInt(parts[4].trim());
        String extra = parts[5].trim();
        String phone = parts.length >= 7 ? parts[6].trim() : "UNKNOWN";

        if ("FLOOD".equals(type)) {
            int waterLevel = Integer.parseInt(extra);
            return new FloodRequest(nextId, location, lat, lng, people, waterLevel, "SMS", phone);
        } else if ("MEDICAL".equals(type)) {
            boolean critical = extra.equalsIgnoreCase("true") || extra.equalsIgnoreCase("y") || extra.equalsIgnoreCase("critical");
            return new MedicalRequest(nextId, location, lat, lng, people, critical, "SMS", phone);
        } else {
            throw new IllegalArgumentException("Unknown distress type: " + type);
        }
    }

    public static Request createMobileAppDistress(
            int nextId, String type, String location, double lat, double lng,
            int people, String extra, String citizenPhone) {
        if ("FLOOD".equalsIgnoreCase(type)) {
            return new FloodRequest(nextId, location, lat, lng, people, Integer.parseInt(extra), "MOBILE_APP", citizenPhone);
        } else {
            boolean critical = extra.equalsIgnoreCase("true") || extra.equalsIgnoreCase("y");
            return new MedicalRequest(nextId, location, lat, lng, people, critical, "MOBILE_APP", citizenPhone);
        }
    }
}
