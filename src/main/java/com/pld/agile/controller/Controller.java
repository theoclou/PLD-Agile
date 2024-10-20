package com.pld.agile.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pld.agile.model.DeliveryRequest;
import com.pld.agile.model.Intersection;
import com.pld.agile.model.Plan;
import com.pld.agile.model.Section;
import com.pld.agile.model.Round;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class Controller {

    // Instanciation de la carte
    private Plan map = new Plan();

    // Chargement du fichier XML au démarrage de l'application
    public Controller() {
        String filePath = "src/data/petitPlan.xml"; // Remplacez par le chemin réel du fichier XML
        try {
            map.readXml(filePath);
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier XML : " + e.getMessage());
            System.exit(1);  // Arrêter le programme avec un code d'erreur
        }
    }

    @PostMapping("/setCourier")
    public String addCourier(@RequestBody Integer courierNumber) {
        return String.format("Number of couriers set, we now have %d couriers.", courierNumber);
    }

    @GetMapping("/getCouriers")
    public String getCouriers() {
        return "Here are the Couriers";
    }

    @PostMapping("/LoadMap")
    public String loadMap(@RequestBody String fileName) {
        // Load the map into the Plan object (or Graph IDK) but does not display it, displayMap is called later
        return String.format("Plan loaded from %s", fileName);
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
