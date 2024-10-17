package com.pld.agile.model;

public class DeliveryRequest {
    private Intersection deliveryAdress;
    private Courier courier;
    private User user;

    public DeliveryRequest(Intersection deliveryAdress, Courier courier, User user) {
        this.deliveryAdress = deliveryAdress;
        this.courier = courier;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
