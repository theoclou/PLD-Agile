package com.pld.agile.model;

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
}
