package com.pld.agile.model.entity;

public class Section {
    private String originId;
    private String destinationId;
    private double length;
    private String name;

    public Section() {

    }

    public void initialisation(String originId, String destinationId, String name, double length ) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.name = name;
        this.length = length;
    }

    // Getters and toString()
    public String getOrigin() { return originId; }
    public String getDestination() { return destinationId; }
    public double getLength() { return length; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Section{origin='" + originId + "', destination='" + destinationId +
                "', name=" + name + ", length='" + length + "'}";
    }
}
