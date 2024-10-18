package com.pld.agile.model;

public class DeliveryRequest {
    private Intersection deliveryAdress;
    private Courier courier;

    public DeliveryRequest(Intersection deliveryAdress, Courier courier) {
        this.deliveryAdress = deliveryAdress;
        this.courier = courier;
    }

    public Intersection getDeliveryAdress() {
        return deliveryAdress;
    }

    public void setDeliveryAdress(Intersection deliveryAdress) {
        this.deliveryAdress = deliveryAdress;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

}
