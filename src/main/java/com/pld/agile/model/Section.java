package com.pld.agile.model;

public class Section {
    private int originId;
    private int destinationId;
    private double length;
    private String name;

    public Section() {

    }

    public void initialisation(int originId, int destinationId, String name, double length ) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.name = name;
        this.length = length;
    }

    // Getters and toString()
    public int getOrigin() { return originId; }
    public int getDestination() { return destinationId; }
    public double getLength() { return length; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Section{origin='" + originId + "', destination='" + destinationId +
                "', name=" + name + ", length='" + length + "'}";
    }
}
