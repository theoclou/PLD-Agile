package com.pld.agile.model;

class Section {
    private Intersection origin;
    private Intersection destination;
    private double length;
    private String streetName;

    public Section(Intersection origin, Intersection destination, String name, double length ) {
        this.origin = origin;
        this.destination = destination;
        this.streetName = name;
        this.length = length;
    }
    

    public Section() {
    }

    // Getters and toString()
    public Intersection getOrigin() { return origin; }
    public Intersection getDestination() { return destination; }
    public double getLength() { return length; }
    public String getName() { return streetName; }

    @Override
    public String toString() {
        return "Section{origin='" + origin + "', destination='" + destination +
                "', name=" + streetName + ", length='" + length + "'}";
    }


    public void initialisation(String originId, String destinationId, String name, double length2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initialisation'");
    }
}
