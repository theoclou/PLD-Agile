package com.pld.agile.controller;// First, modify the AddDeliveryPointCommand class to work with intersection IDs:

import com.pld.agile.controller.Command;
import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.Round;

public class AddDeliveryPointCommand implements Command {
    private final Round round;
    private final String intersectionId;
    private DeliveryRequest addedRequest;

    public AddDeliveryPointCommand(Round round, String intersectionId) {
        this.round = round;
        this.intersectionId = intersectionId;
    }

    @Override
    public void execute() {
        this.addedRequest = round.addDeliveryIntersection(intersectionId);
    }

    @Override
    public void undo() {
        if (addedRequest != null) {
            round.deleteDeliveryRequest(addedRequest.getDeliveryAdress().getId());
        }
    }

    @Override
    public Round getRound() {
        return this.round; // Retourne le round
    }
}
