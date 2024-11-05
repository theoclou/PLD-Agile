package com.pld.agile.controller;

import com.pld.agile.controller.Command;
import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.Round;

public class DeleteDeliveryCommand implements Command {
    private final Round round;
    private final String deliveryRequestId;
    private DeliveryRequest deletedRequest;

    public DeleteDeliveryCommand(Round round, String deliveryRequestId) {
        this.round = round;
        this.deliveryRequestId = deliveryRequestId;
    }

    @Override
    public void execute() {
        this.deletedRequest = round.getDeliveryRequestById(deliveryRequestId);
        round.deleteDeliveryRequest(deliveryRequestId);
    }

    @Override
    public void undo() {
        if (deletedRequest != null) {
            round.getDeliveryRequestList().add(deletedRequest);
        }
    }

    @Override
    public Round getRound() {
        return this.round;
    }
}