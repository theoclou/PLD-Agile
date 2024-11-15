package com.pld.agile.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pld.agile.model.entity.*;
import com.pld.agile.model.graph.Plan;

/**
 * The {@code Controller} class serves as a RESTful API for managing delivery planning
 * and map operations within the application. It handles various tasks related to
 * courier management, map loading, delivery requests, and computation of delivery tours.
 *
 * <p>
 * This controller provides endpoints for operations such as updating the number of couriers,
 * loading maps and delivery requests from files, computing delivery tours, managing
 * delivery points, defining warehouse locations, and generating reports. It also
 * supports undo and redo functionalities for delivery request modifications.
 * </p>
 *
 * <p>
 * The class is annotated with {@code @RestController} to define RESTful endpoints and
 * {@code @CrossOrigin} to allow cross-origin requests from the React frontend running
 * on {@code http://localhost:3000}.
 * </p>
 *
 * <p>
 * The controller interacts with the {@link Plan} and {@link Round} classes to manage
 * the map and delivery round data respectively. It utilizes the Command design pattern
 * through the {@link CommandManager} to enable undo and redo operations.
 * </p>
 *
 * @see Plan
 * @see Round
 * @see CommandManager
 * @see Command
 *
 * @version 1.0
 * @since 2024-04-27
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class Controller {

    /**
     * Manages the execution, undoing, and redoing of commands.
     * Implements the Command design pattern.
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
     * Default constructor for the {@code Controller} class.
     */
    public Controller() {
    }

    /**
     * Updates the number of available couriers.
     *
     * @param payload A {@code Map} containing the new courier count with key "count".
     * @return {@link ResponseEntity}&lt;{@code Void}&gt; with HTTP 200 OK if successful.
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
     * @return A {@code String} containing courier information.
     */
    @GetMapping("/Couriers")
    public String getCouriers() {
        return "Here are the Couriers";
    }

    /**
     * Loads a map from an XML file and initializes the delivery round.
     * Resets any existing map data before loading the new map.
     *
     * @param file {@link MultipartFile} containing the XML map data.
     * @return {@link ResponseEntity}&lt;{@code Map}&lt;{@code String}, {@code String}&gt;&gt;
     *         with a success or error message.
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
     * Loads delivery requests from a file and associates them with the current round.
     *
     * @param file {@link MultipartFile} containing delivery request data.
     * @return {@link ResponseEntity}&lt;{@code Map}&lt;{@code String}, {@code Object}&gt;&gt;
     *         containing delivery requests, warehouse location, and status message.
     */
    @PostMapping("/loadDelivery")
    public ResponseEntity<Map<String, Object>> loadDelivery(@RequestParam("file") MultipartFile file) {
        commandManager.resetCommandStack();
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "File upload failed: No file selected."));
        }
        // TODO: Empty the delivery request list to avoid duplicates

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
     * destination intersections, length, and street name.
     *
     * @return A {@code Map} containing lists of intersections and detailed section
     *         information.
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
     * @return {@link ResponseEntity}&lt;{@code Map}&lt;{@code String}, {@code Object}&gt;&gt;
     *         indicating the status of tour computation and containing the computed tours.
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
     * @return A {@code String} containing tour information.
     */
    @GetMapping("/tours")
    public String getTours() {
        return "Tours Displayed";
    }

    /**
     * Deletes a delivery request from the system.
     *
     * @param deliveryRequestId The ID of the delivery request to be deleted.
     * @return {@link ResponseEntity}&lt;{@code Map}&lt;{@code String}, {@code String}&gt;&gt;
     *         confirmation message with the deleted request ID.
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
     * Deletes a delivery request from the system and updates the corresponding courier's tour.
     *
     * @param request A {@link Map} containing "deliveryId" to be deleted and "courierId" to update the tour.
     * @return {@link ResponseEntity}&lt;{@code Map}&gt; confirmation message with the deleted request ID
     *         and updated tours, or an error message if the operation fails.
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
            // List<DeliveryTour> updatedTours = round.updateLocalPoint(courierId, deliveryId, -1);
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
     * Adds a delivery request to the system.
     *
     * @param request A {@link Map} containing the "intersectionId" of the selected intersection.
     * @return {@link ResponseEntity}&lt;{@code Map}&gt; confirmation of the action with the added delivery request
     *         and current delivery count, or an error message if the operation fails.
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

        // Create and execute command
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
     * @param request A {@link Map} containing the "intersectionId" to define as the warehouse.
     * @return {@link ResponseEntity}&lt;{@code Map}&gt; with a success or error message and warehouse details.
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

        // Create and execute command
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
     * @param request A {@link Map} containing the "intersectionId" and "courierID" for the delivery point.
     * @return {@link ResponseEntity}&lt;{@code Map}&gt; containing the status and updated delivery tours,
     *         or an error message if the operation fails.
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
            // List<DeliveryTour> updatedTours = round.updateLocalPoint(courierIdInt, intersectionId, 1);
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
     * @return {@link ResponseEntity}&lt;{@code Map}&gt; containing the updated delivery requests and
     *         tours after the undo operation, or an error message if the operation fails.
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
            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Performs a redo operation on the last undone command.
     *
     * @return {@link ResponseEntity}&lt;{@code Map}&gt; containing the updated delivery requests and
     *         tours after the redo operation, or an error message if the operation fails.
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
     * @return {@link ResponseEntity}&lt;{@code Void}&gt; with HTTP 200 OK if successful.
     */
    @PostMapping("/resetCommands")
    public ResponseEntity<Void> resetCommands() {
        commandManager.resetCommandStack();
        return ResponseEntity.ok().build();
    }

    /**
     * Generates and downloads a report of the current delivery tours.
     *
     * @return {@link ResponseEntity}&lt;{@code byte[]}&gt; containing the generated report file,
     *         or an error response if the operation fails.
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
