package com.pld.agile.controller;

import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.entity.Section;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class Controller {

    // Instanciation de la carte
    private Plan map = new Plan();

    // Chargement du fichier XML au démarrage de l'application
    public Controller() {
    }

    @PostMapping("/setCourier")
    public String addCourier(@RequestBody Integer courierNumber) {
        return String.format("Number of couriers set, we now have %d couriers.", courierNumber);
    }

    @GetMapping("/getCouriers")
    public String getCouriers() {
        return "Here are the Couriers";
    }

    @PostMapping("/loadMap")
    public ResponseEntity<Map<String, String>> loadMap(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "File upload failed: No file selected."));
        }

        map.resetMap();

        try {
            map.readXmlbyFile(file);
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
