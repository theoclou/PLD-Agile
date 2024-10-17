package com.pld.agile.model;

class Section {
    private String origin;
    private String destination;
    private double length;
    private String streetName;

    public Section(Intersection origin, Intersection destination, String name, double length ) {
        this.origin = origin;
        this.destination = destination;
        this.name = name;
        this.length = length;
    }

    // Getters and toString()
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getLength() { return length; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Section{origin='" + origin + "', destination='" + destination +
                "', name=" + name + ", length='" + length + "'}";
    }
}
