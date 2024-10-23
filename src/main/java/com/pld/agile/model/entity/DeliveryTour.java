package com.pld.agile.model.entity;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * The {@code DeliveryTour} class represents a delivery tour for a courier.
 * It contains information about the courier, the start and end times of the tour,
 * the list of delivery requests, the route, and the arrival times at each intersection.
 */
public class DeliveryTour {
    private Courier courier;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<DeliveryRequest> deliveryRequests;
    private List<Section> route;
    private Map<Intersection, LocalTime> arrivalTimes;

    /**
     * Constructs a new {@code DeliveryTour} with the specified courier, end time,
     * delivery requests, route, and arrival times. The start time is set to 8:00 AM by default.
     *
     * @param courier           the {@code Courier} assigned to the tour
     * @param endTime           the time at which the tour is expected to end
     * @param deliveryRequests  the list of {@code DeliveryRequest} objects for this tour
     * @param route             the list of {@code Section} objects representing the route
     * @param arrivalTimes      the map of {@code Intersection} to {@code LocalTime}, representing
     *                          the arrival times at each intersection
     */
    public DeliveryTour(Courier courier, LocalTime endTime, List<DeliveryRequest> deliveryRequests, List<Section> route, Map<Intersection, LocalTime> arrivalTimes) {
        this.courier = courier;
        this.startTime = LocalTime.of(8, 0);  // Default start time set to 8:00 AM
        this.endTime = endTime;
        this.deliveryRequests = deliveryRequests;
        this.route = route;
        this.arrivalTimes = arrivalTimes;
    }

    /**
     * Returns the courier assigned to this delivery tour.
     *
     * @return the {@code Courier} for this tour
     */
    public Courier getCourier() {
        return this.courier;
    }

    /**
     * Sets the courier for this delivery tour.
     *
     * @param courier the {@code Courier} to assign to the tour
     */
    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    /**
     * Returns the start time of the delivery tour.
     *
     * @return the start time as {@code LocalTime}
     */
    public LocalTime getStartTime() {
        return this.startTime;
    }

    /**
     * Sets the start time of the delivery tour.
     *
     * @param startTime the {@code LocalTime} to set as the start time
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the end time of the delivery tour.
     *
     * @return the end time as {@code LocalTime}
     */
    public LocalTime getEndTime() {
        return this.endTime;
    }

    /**
     * Sets the end time of the delivery tour.
     *
     * @param endTime the {@code LocalTime} to set as the end time
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns the list of delivery requests for this tour.
     *
     * @return the list of {@code DeliveryRequest} objects
     */
    public List<DeliveryRequest> getDeliveryRequests() {
        return this.deliveryRequests;
    }

    /**
     * Sets the list of delivery requests for this tour.
     *
     * @param deliveryRequests the list of {@code DeliveryRequest} objects to set
     */
    public void setDeliveryRequests(List<DeliveryRequest> deliveryRequests) {
        this.deliveryRequests = deliveryRequests;
    }

    /**
     * Returns the route of the delivery tour as a list of sections.
     *
     * @return the route as a list of {@code Section} objects
     */
    public List<Section> getRoute() {
        return this.route;
    }

    /**
     * Sets the route of the delivery tour.
     *
     * @param route the list of {@code Section} objects representing the route
     */
    public void setRoute(List<Section> route) {
        this.route = route;
    }

    /**
     * Returns the arrival times at each intersection in the delivery tour.
     *
     * @return a map of {@code Intersection} to {@code LocalTime}, representing the arrival times
     */
    public Map<Intersection, LocalTime> getArrivalTimes() {
        return this.arrivalTimes;
    }

    /**
     * Sets the arrival times at each intersection for the delivery tour.
     *
     * @param arrivalTimes the map of {@code Intersection} to {@code LocalTime} to set
     */
    public void setArrivalTimes(Map<Intersection, LocalTime> arrivalTimes) {
        this.arrivalTimes = arrivalTimes;
    }
}
