package com.pld.agile.controller;

import java.util.ArrayList;
import java.util.List;

import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.DeliveryTour;
import com.pld.agile.model.entity.Round;

/**
 * The {@code AddDeliveryPointCommand} class implements the {@link Command} interface
 * and provides functionality to add a delivery point to a specific round.
 * It also supports undoing the addition of a delivery point.
 */
public class AddDeliveryPointCommand implements Command {

    private final Round round;
    private final String intersectionId;
    private final Integer courierId;
    private DeliveryRequest addedRequest;
    private List<DeliveryTour> updatedTours = new ArrayList<>(); // New field to store updated tours

    /**
     * Constructs an {@code AddDeliveryPointCommand} with the specified round, intersection ID,
     * and courier ID.
     *
     * @param round          The {@link Round} object where the delivery point will be added.
     * @param intersectionId The ID of the intersection to add as a delivery point.
     * @param courierId      The ID of the courier to update the delivery tours, or -1 if no courier is specified.
     */
    public AddDeliveryPointCommand(Round round, String intersectionId, Integer courierId) {
        this.round = round;
        this.intersectionId = intersectionId;
        this.courierId = courierId;
    }

    /**
     * Executes the command by adding a delivery point to the round and updating the
     * delivery tours accordingly.
     */
    @Override
    public void execute() {
        this.addedRequest = round.addDeliveryIntersection(intersectionId);
        if (courierId >= 0) {
            this.updatedTours = round.updateLocalPoint(courierId, addedRequest.getDeliveryAdress().getId(), 1); // Update tours
        }
        round.setTourAttribution(this.updatedTours); // Update tourAttribution in Round
    }

    /**
     * Undoes the command by removing the added delivery point from the round
     * and reverting the delivery tours to their previous state.
     */
    @Override
    public void undo() {
        if (addedRequest != null) {
            round.deleteDeliveryRequest(addedRequest.getDeliveryAdress().getId());
            if (courierId >= 0) {
                round.updateLocalPoint(courierId, addedRequest.getDeliveryAdress().getId(), -1); // Undo tours
            }
            round.setTourAttribution(this.updatedTours); // Update tourAttribution in Round
        }
    }

    /**
     * Retrieves the {@link Round} associated with this command.
     *
     * @return The {@link Round} object.
     */
    @Override
    public Round getRound() {
        return this.round;
    }

    /**
     * Retrieves the updated delivery tours after the execution of this command.
     *
     * @return A list of {@link DeliveryTour} objects representing the updated tours.
     */
    public List<DeliveryTour> getUpdatedTours() {
        return this.updatedTours;
    }
}
