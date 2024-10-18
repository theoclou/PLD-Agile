package com.pld.agile.model;

class Intersection {
    private String id;
    private double latitude;
    private double longitude;

    public Intersection() {}


    public Intersection init(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }
    // Getters and toString()
    public String getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return "Intersection{id='" + id + "', latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}
