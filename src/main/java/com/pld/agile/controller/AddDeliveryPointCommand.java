package com.pld.agile.controller;// First, modify the AddDeliveryPointCommand class to work with intersection IDs:

import com.pld.agile.controller.Command;
import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.DeliveryTour;
import com.pld.agile.model.entity.Round;

import java.util.ArrayList;
import java.util.List;

public class AddDeliveryPointCommand implements Command {
    private final Round round;
    private final String intersectionId;
    private final Integer courierId;
    private DeliveryRequest addedRequest;
    private List<DeliveryTour> updatedTours = new ArrayList<>(); // New field to store updated tours

    public AddDeliveryPointCommand(Round round, String intersectionId, Integer courierId) {
        this.round = round;
        this.intersectionId = intersectionId;
        this.courierId = courierId;
    }

    @Override
    public void execute() {
        this.addedRequest = round.addDeliveryIntersection(intersectionId);
        if (courierId >= 0) this.updatedTours = round.updateLocalPoint(courierId, addedRequest.getDeliveryAdress().getId(), 1); // Update tours
        round.setTourAttribution(this.updatedTours); // Update tourAttribution in Round
    }

    @Override
    public void undo() {
        if (addedRequest != null) {
            round.deleteDeliveryRequest(addedRequest.getDeliveryAdress().getId());
            if (courierId >= 0) round.updateLocalPoint(courierId, addedRequest.getDeliveryAdress().getId(), -1); // Undo tours
            round.setTourAttribution(this.updatedTours); // Update tourAttribution in Round
        }
    }

    @Override
    public Round getRound() {
        return this.round;
    }

    public List<DeliveryTour> getUpdatedTours() {
        return this.updatedTours;
    }
}
