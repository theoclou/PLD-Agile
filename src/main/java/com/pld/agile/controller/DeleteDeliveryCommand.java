package com.pld.agile.controller;

import java.util.ArrayList;
import java.util.List;

import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.DeliveryTour;
import com.pld.agile.model.entity.Round;

/**
 * The {@code DeleteDeliveryCommand} class implements the {@link Command} interface
 * and provides functionality to delete a delivery request from a delivery round.
 * It also allows undoing the deletion, restoring the deleted request.
 */
public class DeleteDeliveryCommand implements Command {

    /**
     * The {@code Round} instance from which the delivery request will be deleted.
     */
    private final Round round;

    /**
     * The unique identifier of the delivery request to be deleted.
     */
    private final String deliveryRequestId;

    /**
     * The ID of the courier associated with the delivery request, if applicable.
     */
    private final Integer courierId;

    /**
     * Stores the delivery request that was deleted for undo functionality.
     */
    private DeliveryRequest deletedRequest;

    /**
     * Stores the updated list of delivery tours after the command is executed.
     */
    private List<DeliveryTour> updatedTours = new ArrayList<>();

    /**
     * Constructs a new {@code DeleteDeliveryCommand} for a specific delivery request.
     *
     * @param round            the {@code Round} from which the delivery request will be deleted
     * @param deliveryRequestId the ID of the delivery request to delete
     * @param courierId        the ID of the courier associated with the delivery request
     */
    public DeleteDeliveryCommand(Round round, String deliveryRequestId, Integer courierId) {
        this.round = round;
        this.deliveryRequestId = deliveryRequestId;
        this.courierId = courierId;
    }

    /**
     * Executes the command by deleting the specified delivery request from the round.
     * If a courier ID is provided, updates the delivery tours for the courier.
     */
    @Override
    public void execute() {
        this.deletedRequest = round.getDeliveryRequestById(deliveryRequestId);

        if (courierId >= 0) {
            this.updatedTours = round.updateLocalPoint(courierId, deletedRequest.getDeliveryAdress().getId(), -1); // Update tours
        }

        round.deleteDeliveryRequest(deliveryRequestId);
    }

    /**
     * Undoes the command by restoring the deleted delivery request to the round.
     * If a courier ID is provided, updates the delivery tours for the courier.
     */
    @Override
    public void undo() {
        if (deletedRequest != null) {
            round.getDeliveryRequestList().add(deletedRequest);

            if (courierId >= 0) {
                round.updateLocalPoint(courierId, deletedRequest.getDeliveryAdress().getId(), 1); // Undo tours
            }
        }
    }

    /**
     * Returns the {@code Round} instance associated with this command.
     *
     * @return the {@code Round} where the delivery request was deleted
     */
    @Override
    public Round getRound() {
        return this.round;
    }

    /**
     * Returns the updated list of delivery tours after the command was executed.
     *
     * @return the updated list of {@code DeliveryTour}
     */
    public List<DeliveryTour> getUpdatedTours() {
        return this.updatedTours;
    }
}
