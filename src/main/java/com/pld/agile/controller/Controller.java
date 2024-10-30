package com.pld.agile.controller;

import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.entity.Section;
import com.pld.agile.model.entity.Round;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;


/**
 * REST Controller for managing delivery planning and map operations.
 * This controller handles operations related to courier management, map loading,
 * delivery requests, and tour computation.
 *
 * @RestController annotation indicates that this class serves REST endpoints
 * @CrossOrigin allows requests from the React frontend running on localhost:3000
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class Controller {

    /**
     * Represents the city map with intersections and sections.
     */
    private Plan map = new Plan();

    /**
     * Represents a delivery round containing multiple delivery requests.
     */
    private Round round = new Round();

    /**
     * The number of available couriers for deliveries.
     */
    private int numberOfCouriers = 2;

    /**
     * Default constructor for the Controller class.
     */
    public Controller() {
    }

    /**
     * Updates the number of available couriers.
     *
     * @param payload A map containing the new courier count with key "count"
     * @return ResponseEntity<Void> with HTTP 200 OK if successful
     */
    @PostMapping("/courriers")
    public ResponseEntity<Void> updateCouriers(@RequestBody Map<String, Integer> payload) {
        numberOfCouriers = payload.get("count");
        System.out.println("Update courriers : " + numberOfCouriers);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves information about available couriers.
     *
     * @return String containing courier information
     */
    @GetMapping("/Courriers")
    public String getCouriers() {
        return "Here are the Couriers";
    }

    /**
     * Loads a map from an XML file and initializes the delivery round.
     * Resets any existing map data before loading the new map.
     *
     * @param file MultipartFile containing the XML map data
     * @return ResponseEntity with a success/error message
     * @throws IOException if there's an error reading the file
     */
    @PostMapping("/loadMap")
    public ResponseEntity<Map<String, String>> loadMap(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "File upload failed: No file selected."));
        }

        map.resetMap();

        try {
            map.readXmlbyFile(file);
            round = new Round();
            round.init(numberOfCouriers, map);
            round.clearDeliveryRequests();
            if (map.getIntersections().size() > 0) {
                return ResponseEntity.ok(Collections.singletonMap("message", "Plan loaded successfully."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "No valid intersections loaded. Please check the file."));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error loading map: " + e.getMessage()));
        }
    }

    /**
     * Loads delivery requests from a file and associates them with the current round.
     *
     * @param file MultipartFile containing delivery request data
     * @return ResponseEntity containing delivery requests, warehouse location, and status message
     * @throws IOException if there's an error reading the file
     */
    @PostMapping("/loadDelivery")
    public ResponseEntity<Map<String, Object>> loadDelivery(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "File upload failed: No file selected."));
        }
        //TODO empty the delivery request list to avoid duplicates

        try {
            //Create response object
            Map<String, Object> response = new HashMap<>();
            System.out.println("File received: " + file.getOriginalFilename());
            round.loadRequestsByfile(file);
            List<DeliveryRequest> deliveryRequestList = round.getDeliveryRequestList();
            System.out.println("Delivery request list size: " + deliveryRequestList.size());
            response.put("deliveries", deliveryRequestList);
            response.put("warehouse", round.getWarehouse());
            response.put("message", "Delivery points loaded successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error loading map: " + e.getMessage()));
        }
    }

    /**
     * Retrieves the current map data including intersections and sections.
     * Returns detailed information about each section including origin and destination
     * intersections, length, and street name.
     *
     * @return Map containing lists of intersections and detailed section information
     */
    @GetMapping("/map")
    public Map<String, Object> displayMap() {
        Map<String, Object> response = new HashMap<>();

        List<Intersection> intersections = map.getIntersections();
        List<Section> sections = map.getSections();

        // Construct list for the front
        List<Map<String, Object>> detailedSections = new ArrayList<>();

        for (Section section : sections) {
            String originId = section.getOrigin();
            String destinationId = section.getDestination();

            Intersection originIntersection = map.getIntersectionById(originId);
            Intersection destinationIntersection = map.getIntersectionById(destinationId);

            if (originIntersection != null && destinationIntersection != null) {
                Map<String, Object> sectionDetails = new HashMap<>();
                sectionDetails.put("origin", originIntersection);
                sectionDetails.put("destination", destinationIntersection);
                sectionDetails.put("length", section.getLength());
                sectionDetails.put("name", section.getName());

                detailedSections.add(sectionDetails);
            }
        }

        response.put("intersections", intersections);
        response.put("sections", detailedSections);

        return response;
    }

    /**
     * Computes delivery tours based on current delivery requests and courier availability.
     *
     * @return String indicating the status of tour computation
     */
    @PostMapping("/compute")
    public String computeTours() {
        return "Tours Computed";
    }

    /**
     * Retrieves computed delivery tours.
     *
     * @return String containing tour information
     */
    @GetMapping("/tours")
    public String getTours() {
        return "Tours Displayed";
    }

    /**
     * Adds a new delivery request to the system.
     *
     * @param deliveryRequest The delivery request to be added
     * @return String confirmation message with request details
     */
    @PostMapping("/addDeliveryRequest")
    public String addDeliveryRequest(@RequestBody DeliveryRequest deliveryRequest) {
        return String.format("Delivery request added: %s", deliveryRequest);
    }

    /**
     * Deletes a delivery request from the system.
     *
     * @param deliveryRequestId The ID of the delivery request to be deleted
     * @return String confirmation message with the deleted request ID
     */
    @DeleteMapping("/deleteDeliveryRequest")
    public ResponseEntity<Map<String, String>> deleteDeliveryRequest(@RequestBody String deliveryRequestId) {
        System.out.println("Received deliveryRequestId: " + deliveryRequestId); // Log to check the id reception

        boolean deleted = round.deleteDeliveryRequest(deliveryRequestId);

        if (deleted) {
            System.out.println(round.getDeliveryRequestList().size());
            return ResponseEntity.ok(Collections.singletonMap("message", "Delivery request deleted successfully."));
        } else {
            System.out.println(round.getDeliveryRequestList().size());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Delivery request not found."));
        }
    }


    /**
     * Adds a delivery request from the system.
     *
     * @param request The intersection object "intersectionId" : id to be added
     * @return String confirmation message with the adding request ID
     */
    @PostMapping("/addDeliveryPointById")
    public ResponseEntity<Object> addDeliveryPoint(@RequestBody Map<String, String> request) {
        String intersectionId = request.get("intersectionId"); // get the intersection id to add
        if (intersectionId == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Intersection ID is required."));
        }
        // Add the delivery point
        DeliveryRequest newDeliveryRequest = round.addDeliveryIntersection(intersectionId);
        if (newDeliveryRequest != null) {
            System.out.println("Point successfully added: " + intersectionId);
            return ResponseEntity.ok(newDeliveryRequest);
        } else {
            System.out.println(round.getDeliveryRequestList().size());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Delivery request not found."));
        }
    }


    /**
     * Validates a delivery request.
     *
     * @param deliveryRequestId The ID of the delivery request to be validated
     * @return String confirmation message with the validated request ID
     */
    @PostMapping("/validate")
    public String validateDeliveryRequest(@RequestBody Integer deliveryRequestId) {
        return String.format("Delivery request validated: %s", deliveryRequestId);
    }
}
