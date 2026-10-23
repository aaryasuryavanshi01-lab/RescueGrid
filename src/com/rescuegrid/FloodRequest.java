package com.rescuegrid;

public class FloodRequest extends Request {
    private int waterLevel;

    public FloodRequest(int id, String location, int people, int waterLevel) {
        super(id, location, people);
        this.waterLevel = waterLevel;
    }

    public FloodRequest(int id, String location, double latitude, double longitude, int people, int waterLevel, String sourceChannel, String reporterPhone) {
        super(id, location, latitude, longitude, people, sourceChannel, reporterPhone);
        this.waterLevel = waterLevel;
    }

    public int getWaterLevel() { return waterLevel; }

    @Override
    public String getType() { return "FLOOD"; }

    @Override
    public String getExtra() { return "" + waterLevel; }

    @Override
    public int getUrgency() {
        return super.getUrgency() + waterLevel * 8;
    }
}
