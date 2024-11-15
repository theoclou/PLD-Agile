package com.pld.agile.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pld.agile.model.entity.DeliveryRequest;
import com.pld.agile.model.entity.DeliveryTour;
import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.entity.Section;
import com.pld.agile.model.graph.Plan;

/**
 * REST Controller for managing delivery planning and map operations.
 * This controller handles operations related to courier management, map
 * loading,
 * delivery requests, and tour computation.
 *
 * @RestController annotation indicates that this class serves REST endpoints
 * @CrossOrigin allows requests from the React frontend running on
 *              localhost:3000
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class Controller {
    /**
     * Command for undo/redo tasks
     */
    private CommandManager commandManager = new CommandManager();
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
    @PostMapping("/couriers")
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
    @GetMapping("/Couriers")
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
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "File upload failed: No file selected."));
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Collections.singletonMap("error", "No valid intersections loaded. Please check the file."));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Error loading map: " + e.getMessage()));
        }
    }

    /**
     * Loads delivery requests from a file and associates them with the current
     * round.
     *
     * @param file MultipartFile containing delivery request data
     * @return ResponseEntity containing delivery requests, warehouse location, and
     *         status message
     * @throws IOException if there's an error reading the file
     */
    @PostMapping("/loadDelivery")
    public ResponseEntity<Map<String, Object>> loadDelivery(@RequestParam("file") MultipartFile file) {
        commandManager.resetCommandStack();
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "File upload failed: No file selected."));
        }
        // TODO empty the delivery request list to avoid duplicates

        try {
            // Create response object
            Map<String, Object> response = new HashMap<>();
            round.loadRequests(file);
            List<DeliveryRequest> deliveryRequestList = round.getDeliveryRequestList();
            System.out.println("Delivery request list size: " + deliveryRequestList.size());
            response.put("deliveries", deliveryRequestList);
            response.put("warehouse", round.getWarehouse());
            response.put("message", "Delivery points loaded successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Error loading map: " + e.getMessage()));
        }
    }

    /**
     * Retrieves the current map data including intersections and sections.
     * Returns detailed information about each section including origin and
     * destination
     * intersections, length, and street name.
     *
     * @return Map containing lists of intersections and detailed section
     *         information
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
     * Computes delivery tours based on current delivery requests and courier
     * availability.
     *
     * @return String indicating the status of tour computation
     */
    @PostMapping("/compute")
    public ResponseEntity<Map<String, Object>> computeTours() {
        Map<String, Object> response = new HashMap<>();
        try {
            commandManager.resetCommandStack();

            map.softResetMap();
            map.preprocessData();

            round.softReset();
            round.init(numberOfCouriers, map);
            round.computeRoundOptimized();
            List<DeliveryTour> tourAttribution = round.getTourAttribution();

            response.put("status", "success");
            response.put("message", "Tours computed successfully");
            response.put("tours", tourAttribution);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to compute tours: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
     * Deletes a delivery request from the system.
     *
     * @param deliveryRequestId The ID of the delivery request to be deleted
     * @return String confirmation message with the deleted request ID
     */
    @DeleteMapping("/deleteDeliveryRequest")
    public ResponseEntity<Map<String, String>> deleteDeliveryRequest(@RequestBody String deliveryRequestId) {
        System.out.println("Received deliveryRequestId: " + deliveryRequestId);
        Map<String, String> response = new HashMap<>();

        // Create and execute delete command
        DeleteDeliveryCommand command = new DeleteDeliveryCommand(round, deliveryRequestId, -1);
        commandManager.executeCommand(command);

        // Round is automatically updated after command execution
        System.out.println("Nombre de livraisons restantes : " + round.getDeliveryRequestList().size());

        response.put("message", "Delivery request deleted successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a delivery request from the system.
     *
     * @param request Request containing "deliveryId" to be deleted and "courierId"
     *                to update the tour
     * @return String confirmation message with the deleted request ID
     */
    @DeleteMapping("/deleteDeliveryRequestWithCourier")
    public ResponseEntity<Map<String, Object>> deleteDeliveryRequestWithCourier(
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("Received request: " + request);
        String deliveryId = request.get("deliveryId");
        String courierIdStr = request.get("courierId");

        if (deliveryId == null || courierIdStr == null) {
            response.put("status", "error");
            response.put("message", "Delivery ID and Courier ID are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            int courierId = Integer.parseInt(courierIdStr);

            // Delete the delivery request from the list
            DeleteDeliveryCommand command = new DeleteDeliveryCommand(round, deliveryId, courierId);
            commandManager.executeCommand(command);

            // Update the tour
            // List<DeliveryTour> updatedTours = round.updateLocalPoint(courierId,
            // deliveryId, -1);
            List<DeliveryTour> updatedTours = round.getTourAttribution();

            if (updatedTours != null) {
                response.put("status", "success");
                response.put("message", "Delivery point deleted successfully");
                response.put("tours", updatedTours);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete delivery point");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (NumberFormatException e) {
            response.put("status", "error");
            response.put("message", "Invalid courier ID format");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting delivery point: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Add a delivery request from the system.
     *
     * @param request (json which contains the id of the selected intersection)
     * @return Confirmation of the action
     */
    @PostMapping("/addDeliveryPointById")
    public ResponseEntity<Map<String, Object>> addDeliveryPoint(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String intersectionId = request.get("intersectionId");
        if (intersectionId == null) {
            response.put("status", "error");
            response.put("message", "Intersection ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        // create and execute command
        AddDeliveryPointCommand command = new AddDeliveryPointCommand(round, intersectionId, -1);
        commandManager.executeCommand(command);

        DeliveryRequest newDeliveryRequest = round.getDeliveryRequestById(intersectionId);
        if (newDeliveryRequest != null) {
            response.put("status", "success");
            response.put("message", "Delivery point added successfully");
            response.put("deliveryRequest", newDeliveryRequest);
            response.put("currentDeliveryCount", round.getDeliveryRequestList().size());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to add delivery point");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Defines a warehouse location based on an intersection ID.
     *
     * @param request A map containing the intersection ID to define as the
     *                warehouse.
     * @return {@link ResponseEntity} with a success or error message.
     */
    @PostMapping("/defineWarehouseById")
    public ResponseEntity<Map<String, Object>> defineWarehouse(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String intersectionId = request.get("intersectionId");
        if (intersectionId == null) {
            response.put("status", "error");
            response.put("message", "Intersection ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        // create and execute command
        DefineWarehousePointCommand command = new DefineWarehousePointCommand(round, intersectionId);
        commandManager.executeCommand(command);

        Intersection newIntersection = round.defineWarehousePoint(intersectionId);
        if (newIntersection != null) {
            response.put("status", "success");
            response.put("message", "Warehouse point added successfully");
            response.put("Warehouse", newIntersection);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to define the warehouse");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Adds a delivery point after the computation of delivery tours.
     *
     * @param request A map containing the intersection ID and courier ID for the
     *                delivery point.
     * @return {@link ResponseEntity} containing the status and updated delivery
     *         tours.
     */
    @PostMapping("/addDeliveryPointByIdAfterCompute")
    public ResponseEntity<Map<String, Object>> addDeliveryPointAfterCompute(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Request received : " + request);
        String intersectionId = request.get("intersectionId");
        if (intersectionId == null) {
            response.put("status", "error");
            response.put("message", "Intersection ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        String courierId = request.get("courierID");
        if (courierId == null) {
            response.put("status", "error");
            response.put("message", "Courier ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // First update the list
            Integer courierIdInt = Integer.parseInt(courierId);

            AddDeliveryPointCommand command = new AddDeliveryPointCommand(round, intersectionId, courierIdInt);
            commandManager.executeCommand(command);

            // Then the tour
            // List<DeliveryTour> updatedTours = round.updateLocalPoint(courierIdInt,
            // intersectionId, 1);
            List<DeliveryTour> updatedTours = round.getTourAttribution();

            if (updatedTours != null) {
                response.put("status", "success");
                response.put("message", "Delivery point added successfully");
                response.put("tours", updatedTours);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to add delivery point");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    /**
     * Performs an undo operation on the last executed command.
     *
     * @return {@link ResponseEntity} containing the updated delivery requests and
     *         tours after the undo operation.
     */
    @PostMapping("/undo")
    public ResponseEntity<Map<String, Object>> undo() {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Undoing");

        try {
            System.out.println("Old round : " + round.getDeliveryRequestList().size());
            commandManager.undo();
            Command lastCommand = commandManager.getLastCommand();
            Round currentRound = lastCommand != null ? lastCommand.getRound() : round;
            System.out.println("New round : " + currentRound.getDeliveryRequestList().size());
            response.put("status", "success");
            response.put("message", "Undo successful");
            response.put("currentDeliveryCount", currentRound.getDeliveryRequestList().size());
            response.put("deliveryRequests", currentRound.getDeliveryRequestList());
            if (currentRound.getTourAttribution() != null) {
                response.put("tours", currentRound.getTourAttribution());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to undo: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
            throw e;
            // return
            // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Performs a redo operation on the last undone command.
     *
     * @return {@link ResponseEntity} containing the updated delivery requests and
     *         tours after the redo operation.
     */
    @PostMapping("/redo")
    public ResponseEntity<Map<String, Object>> redo() {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Redoing");
        try {
            System.out.println("Old round : " + round.getDeliveryRequestList().size());
            commandManager.redo();
            Command lastCommand = commandManager.getLastCommand();
            Round currentRound = lastCommand != null ? lastCommand.getRound() : round;

            response.put("status", "success");
            response.put("message", "Redo successful");
            response.put("currentDeliveryCount", currentRound.getDeliveryRequestList().size());
            response.put("deliveryRequests", currentRound.getDeliveryRequestList());
            if (currentRound.getTourAttribution() != null)
                response.put("tours", currentRound.getTourAttribution());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Resets the command stack, clearing all executed and undone commands.
     *
     * @return {@link ResponseEntity} with HTTP 200 OK if successful.
     */
    @PostMapping("/resetCommands")
    public ResponseEntity<Void> resetCommands() {
        commandManager.resetCommandStack();
        return ResponseEntity.ok().build();
    }

    /**
     * Generates and downloads a report of the current delivery tours.
     *
     * @return {@link ResponseEntity} containing the generated report file.
     */
    @GetMapping("/downloadReport")
    public ResponseEntity<byte[]> downloadReport() {
        try {
            String content = round.generateTourReport();
            byte[] reportBytes = content.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment",
                    "delivery_tours_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                            + ".txt");

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
