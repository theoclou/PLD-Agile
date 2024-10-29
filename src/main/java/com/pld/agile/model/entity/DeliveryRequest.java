package com.pld.agile.model.entity;

/**
 * The {@code DeliveryRequest} class represents a delivery request, which includes a delivery
 * address and an assigned courier. This class provides methods to retrieve and update
 * the delivery address and courier.
 */
public class DeliveryRequest {
    private Intersection deliveryAdress;
    private Courier courier;

    public DeliveryRequest() {
    }

    /**
     * Constructs a new {@code DeliveryRequest} with the specified delivery address.
     *
     * @param deliveryAdress the {@code Intersection} representing the delivery address
     */
    public DeliveryRequest(Intersection deliveryAdress) {
        this.deliveryAdress = deliveryAdress;
    }

    /**
     * Returns the delivery address associated with this delivery request.
     *
     * @return the delivery address as an {@code Intersection}
     */
    public Intersection getDeliveryAdress() {
        return deliveryAdress;
    }

    /**
     * Sets the delivery address for this delivery request.
     *
     * @param deliveryAdress the new delivery address as an {@code Intersection}
     */
    public void setDeliveryAdress(Intersection deliveryAdress) {
        this.deliveryAdress = deliveryAdress;
    }

    /**
     * Returns the courier assigned to this delivery request.
     *
     * @return the assigned {@code Courier}, or {@code null} if no courier is assigned
     */
    public Courier getCourier() {
        return courier;
    }

    /**
     * Assigns a courier to this delivery request.
     *
     * @param courier the {@code Courier} to assign to this delivery request
     */
    public void setCourier(Courier courier) {
        this.courier = courier;
    }

}
