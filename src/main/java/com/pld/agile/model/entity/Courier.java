package com.pld.agile.model.entity;

/**
 * The {@code Courier} class represents a courier with an associated ID.
 * This class provides methods to retrieve the courier's ID.
 */
public class Courier {
    private Integer id;

    /**
     * Constructs a new {@code Courier} with the specified ID.
     *
     * @param id the unique identifier for the courier
     */
    public Courier(Integer id) {
        this.id = id;
    }

    /**
     * Returns the unique identifier for this courier.
     *
     * @return the courier's ID as an {@code Integer}
     */
    public Integer getId() {
        return this.id;
    }
}
