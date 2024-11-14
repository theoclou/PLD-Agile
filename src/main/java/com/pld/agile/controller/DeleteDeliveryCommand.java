package com.pld.agile.controller;

import com.pld.agile.controller.Command;
import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.entity.DeliveryTour;

import java.util.ArrayList;
import java.util.List;

public class DeleteDeliveryCommand implements Command {
    private final Round round;
    private final String deliveryRequestId;
    private final Integer courierId;
    private DeliveryRequest deletedRequest;
    private List<DeliveryTour> updatedTours = new ArrayList<>(); // New field to store updated tours

    public DeleteDeliveryCommand(Round round, String deliveryRequestId, Integer courierId) {
        this.round = round;
        this.deliveryRequestId = deliveryRequestId;
        this.courierId = courierId;
    }

    @Override
    public void execute() {
        this.deletedRequest = round.getDeliveryRequestById(deliveryRequestId);
        if (courierId >= 0) this.updatedTours = round.updateLocalPoint(courierId, deletedRequest.getDeliveryAdress().getId(), -1); // Update tours
        round.deleteDeliveryRequest(deliveryRequestId);
    }

    @Override
    public void undo() {
        if (deletedRequest != null) {
            round.getDeliveryRequestList().add(deletedRequest);
            if(courierId >= 0) round.updateLocalPoint(courierId, deletedRequest.getDeliveryAdress().getId(), 1); // Undo tours
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