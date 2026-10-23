package com.rescuegrid;

public class MedicalRequest extends Request {
    private boolean critical;

    public MedicalRequest(int id, String location, int people, boolean critical) {
        super(id, location, people);
        this.critical = critical;
    }

    public MedicalRequest(int id, String location, double latitude, double longitude, int people, boolean critical, String sourceChannel, String reporterPhone) {
        super(id, location, latitude, longitude, people, sourceChannel, reporterPhone);
        this.critical = critical;
    }

    public boolean isCritical() { return critical; }

    @Override
    public String getType() { return "MEDICAL"; }

    @Override
    public String getExtra() { return "" + critical; }

    @Override
    public int getUrgency() {
        if (critical) {
            return super.getUrgency() + 40;
        }
        return super.getUrgency() + 10;
    }
}
