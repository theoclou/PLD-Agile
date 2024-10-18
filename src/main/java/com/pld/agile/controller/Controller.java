package com.pld.agile.controller;

import com.pld.agile.model.DeliveryRequest;
import com.pld.agile.model.Round;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {


    @PostMapping("/setCourier")
    public String addCourier(@RequestBody Integer courierNumber) {
        // Change the number of courier in DeliveryTour (not sure where) and return the success
        return String.format("Number of couriers set, we know have %d couriers.", courierNumber);
    }

    @GetMapping("/getCouriers")
    public String getCouriers() {
        // Return the number of couriers and maybe the list if we decide it is needed ?
        return "Here are the Couriers";
    }

    @PostMapping("/LoadMap")
    public String loadMap(@RequestBody String fileName) {
        // Load the map into the Plan object (or Graph IDK) but does not display it, displayMap is called later
        return String.format("Plan loaded from %s", fileName);
    }

    @GetMapping("/map")
    public String displayMap() {
        // Return a json containing all data needed to display the map (either the list of intersection or nothing if there is a React component that manage a map
        return "Hello World";
    }

    @PostMapping("/compute")
    public String computeTours() {
        // Compute the Tours from the Graph and List of Delivery Request. I put no parameters since we should be able to retrieve them from the code I hope ?
        return "Tours Computed";
    }

    @GetMapping("/tours")
    public String getTours() {
        // Return the Tours as a list of lists of Intersection
        return "Tours Displayed";
    }

    @PostMapping("/addDeliveryRequest")
    public String addDeliveryRequest(@RequestBody DeliveryRequest deliveryRequest) {
        // Create a DeliveryRequest from what is given in the body
        return String.format("Delivery request added: %s", deliveryRequest);
    }

    @DeleteMapping("/deleteDeliveryRequest")
    public String deleteDeliveryRequest(@RequestBody Integer deliveryRequestId) {
        // Delete the DeliveryRequest with the id given in the body
        return String.format("Delivery request removed: n°%s", deliveryRequestId);
    }

    @PostMapping("/validate")
    public String validateDeliveryRequest(@RequestBody Integer deliveryRequestId) {
        // Validate the DeliveryRequest with the id given in the body
        return String.format("Delivery request validated: %s", deliveryRequestId);
    }


}
