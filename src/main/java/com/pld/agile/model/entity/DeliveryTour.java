package com.pld.agile.model.entity;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class DeliveryTour {
    private Courier courier;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<DeliveryRequest> deliveryRequests;
    private List<Section> route;
    private Map<Intersection, LocalTime> arrivalTimes;

    public DeliveryTour(Courier courier, LocalTime endTime, List<DeliveryRequest> deliveryRequests, List<Section> route, Map<Intersection, LocalTime> arrivalTimes) {
        this.courier = courier;
        this.startTime = LocalTime.of(8,0);
        this.endTime = endTime;
        this.deliveryRequests = deliveryRequests;
        this.route = route;
        this.arrivalTimes = arrivalTimes;
    }

    public Courier getCourier() {
        return this.courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List<DeliveryRequest> getDeliveryRequests() {
        return this.deliveryRequests;
    }

    public void setDeliveryRequests(List<DeliveryRequest> deliveryRequests) {
        this.deliveryRequests = deliveryRequests;
    }

    public List<Section> getRoute() {
        return this.route;
    }

    public void setRoute(List<Section> route) {
        this.route = route;
    }

    public Map<Intersection,LocalTime> getArrivalTimes() {
        return this.arrivalTimes;
    }

    public void setArrivalTimes(Map<Intersection,LocalTime> arrivalTimes) {
        this.arrivalTimes = arrivalTimes;
    }

}
