package com.pld.agile.model;

public class DeliveryRequest {
    private Intersection deliveryAdress;
    private Courier courier;

    public DeliveryRequest(Intersection deliveryAdress) {
        this.deliveryAdress = deliveryAdress;
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
