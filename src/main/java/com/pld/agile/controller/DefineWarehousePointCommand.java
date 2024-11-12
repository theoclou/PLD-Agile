package com.pld.agile.controller;// First, modify the AddDeliveryPointCommand class to work with intersection IDs:


import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Round;

public class DefineWarehousePointCommand implements Command {
    private final Round round;
    private final String intersectionId;
    private Intersection warehouse;

    public DefineWarehousePointCommand(Round round, String intersectionId) {
        this.round = round;
        this.intersectionId = intersectionId;
    }

    @Override
    public void execute() {
        this.warehouse = round.defineWarehousePoint(intersectionId);
    }

    @Override
    public void undo() {
        if (warehouse != null) {
            round.deleteWarehouse();
        }
    }

    @Override
    public Round getRound() {
        return this.round; // Retourne le round
    }
}
