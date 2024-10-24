package com.pld.agile.controller;

import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.entity.Section;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.IOException;

import com.pld.agile.model.XMLReader;

import javax.management.InstanceNotFoundException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class Controller {

    private Plan map = new Plan();
    private Round round = new Round();
    private int numberOfCouriers = 2;
    private XMLReader reader=new XMLReader();
    public Controller() {
    }

    @PostMapping("/courriers")
    public ResponseEntity<Void> updateCouriers(@RequestBody Map<String, Integer> payload) {
        numberOfCouriers = payload.get("count");
        System.out.println("Update courriers : " + numberOfCouriers);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/Courriers")
    public String getCouriers() {
        return "Here are the Couriers";
    }

    @PostMapping("/loadMap")
    public ResponseEntity<Map<String, String>> loadMap(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "File upload failed: No file selected."));
        }

        try {
            map.PlanInit(file);
            round = new Round();
            round.init(numberOfCouriers, map);

            if (!map.getIntersections().isEmpty()) {
                return ResponseEntity.ok(
                        Collections.singletonMap("message", "Plan loaded successfully."));
            } else {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "No valid intersections loaded. Please check the file."));
            }

        } catch (FileNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "File not found: " + e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Invalid file format or data: " + e.getMessage()));

        } catch (InstanceNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Required instance not found: " + e.getMessage()));

        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Unexpected error: " + e.getMessage()));
        }
    }

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
            response.put("message", "Delivery points loaded successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error loading map: " + e.getMessage()));
        }
    }

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


    @PostMapping("/compute")
    public String computeTours() {
        return "Tours Computed";
    }

    @GetMapping("/tours")
    public String getTours() {
        return "Tours Displayed";
    }

    @PostMapping("/addDeliveryRequest")
    public String addDeliveryRequest(@RequestBody DeliveryRequest deliveryRequest) {
        return String.format("Delivery request added: %s", deliveryRequest);
    }

    @DeleteMapping("/deleteDeliveryRequest")
    public String deleteDeliveryRequest(@RequestBody Integer deliveryRequestId) {
        return String.format("Delivery request removed: n°%s", deliveryRequestId);
    }

    @PostMapping("/validate")
    public String validateDeliveryRequest(@RequestBody Integer deliveryRequestId) {
        return String.format("Delivery request validated: %s", deliveryRequestId);
    }
}
