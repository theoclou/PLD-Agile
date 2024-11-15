package com.pld.agile.controller;

import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Round;

/**
 * The {@code DefineWarehousePointCommand} class implements the {@link Command} interface
 * and defines a warehouse point on a specific intersection. It allows for setting a 
 * warehouse in a delivery round and provides undo functionality to remove the warehouse.
 */
public class DefineWarehousePointCommand implements Command {

    /**
     * The {@code Round} instance where the warehouse will be defined.
     */
    private final Round round;

    /**
     * The unique identifier of the intersection where the warehouse will be defined.
     */
    private final String intersectionId;

    /**
     * The {@code Intersection} object representing the defined warehouse.
     */
    private Intersection warehouse;

    /**
     * Constructs a new {@code DefineWarehousePointCommand} for a specific round and intersection.
     *
     * @param round          the {@code Round} instance where the warehouse will be defined
     * @param intersectionId the ID of the intersection to be set as the warehouse
     */
    public DefineWarehousePointCommand(Round round, String intersectionId) {
        this.round = round;
        this.intersectionId = intersectionId;
    }

    /**
     * Executes the command by defining the warehouse at the specified intersection in the round.
     */
    @Override
    public void execute() {
        this.warehouse = round.defineWarehousePoint(intersectionId);
    }

    /**
     * Undoes the command by deleting the warehouse definition from the round.
     */
    @Override
    public void undo() {
        if (warehouse != null) {
            round.deleteWarehouse();
        }
    }

    /**
     * Returns the {@code Round} instance associated with this command.
     *
     * @return the {@code Round} where the warehouse was defined
     */
    @Override
    public Round getRound() {
        return this.round;
    }
}
