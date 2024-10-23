package com.pld.agile.model.entity;

/**
 * The {@code Intersection} class represents an intersection with a unique ID, latitude, and longitude.
 * This class provides methods to initialize and retrieve the details of an intersection.
 */
public class Intersection {
    private String id;
    private double latitude;
    private double longitude;

    /**
     * Constructs a new {@code Intersection} with no initial values. The values should be set
     * later using the {@code initialisation} method.
     */
    public Intersection() {

    }

    /**
     * Initializes the intersection with the specified ID, latitude, and longitude.
     *
     * @param id        the unique identifier of the intersection
     * @param latitude  the latitude coordinate of the intersection
     * @param longitude the longitude coordinate of the intersection
     */
    public void initialisation(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the unique identifier of this intersection.
     *
     * @return the ID of the intersection
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the latitude of this intersection.
     *
     * @return the latitude of the intersection
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude of this intersection.
     *
     * @return the longitude of the intersection
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns a string representation of the intersection, including its ID, latitude, and longitude.
     *
     * @return a string representation of the intersection
     */
    @Override
    public String toString() {
        return "Intersection{id='" + id + "', latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}
