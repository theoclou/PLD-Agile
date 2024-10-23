package com.pld.agile.model.entity;

/**
 * The {@code Section} class represents a road section between two intersections.
 * Each section has an origin, a destination, a name, and a length (distance).
 */
public class Section {
    private String originId;
    private String destinationId;
    private double length;
    private String name;

    /**
     * Constructs an empty {@code Section}. The values should be initialized
     * using the {@code initialisation} method.
     */
    public Section() {
    }

    /**
     * Initializes the section with the specified origin, destination, name, and length.
     *
     * @param originId       the ID of the origin intersection
     * @param destinationId  the ID of the destination intersection
     * @param name           the name of the section (e.g., street name)
     * @param length         the length of the section in kilometers
     */
    public void initialisation(String originId, String destinationId, String name, double length) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.name = name;
        this.length = length;
    }

    /**
     * Returns the ID of the origin intersection for this section.
     *
     * @return the origin intersection ID
     */
    public String getOrigin() {
        return originId;
    }

    /**
     * Returns the ID of the destination intersection for this section.
     *
     * @return the destination intersection ID
     */
    public String getDestination() {
        return destinationId;
    }

    /**
     * Returns the length (distance) of this section.
     *
     * @return the length of the section in kilometers
     */
    public double getLength() {
        return length;
    }

    /**
     * Returns the name of this section, such as the street name.
     *
     * @return the name of the section
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the section, including the origin, destination,
     * name, and length.
     *
     * @return a string representation of the section
     */
    @Override
    public String toString() {
        return "Section{origin='" + originId + "', destination='" + destinationId +
                "', name='" + name + "', length='" + length + "'}";
    }
}
