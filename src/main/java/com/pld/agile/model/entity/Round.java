package com.pld.agile.model.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.management.InstanceNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pld.agile.model.Solver;
import com.pld.agile.model.algorithm.KMeansClustering;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.BnBStrategy;

/**
 * The {@code Round} class represents a round of deliveries managed by a fleet
 * of couriers. It manages the assignment of delivery requests to couriers,
 * computes delivery tours, and tracks the delivery information.
 *
 * <p>
 * This class handles the core functionality of initializing couriers, loading
 * delivery requests from XML files, computing optimized delivery tours using
 * clustering and routing algorithms, and managing the state of delivery rounds.
 * </p>
 *
 * @author 
 * @version 1.0
 * @since 2024-04-27
 */
public class Round {
    /**
     * The speed of the courier in kilometers per hour.
     */
    private static final double COURIER_SPEED = 15.0; // km/h

    /**
     * The plan containing intersections and sections.
     */
    private Plan plan;

    /**
     * The list of couriers participating in the delivery round.
     */
    private List<Courier> courierList = new ArrayList<>();

    /**
     * The list of delivery requests to be fulfilled in the round.
     */
    private List<DeliveryRequest> deliveryRequestList = new ArrayList<>();

    /**
     * The list of delivery tours assigned to couriers.
     */
    private List<DeliveryTour> tourAttribution = new ArrayList<>();

    /**
     * The warehouse location for the delivery round.
     */
    private Intersection warehouse;

    /**
     * The K-Means clustering instance for grouping delivery points.
     */
    private KMeansClustering KNN = new KMeansClustering();

    /**
     * The list of solvers used to compute delivery tours for couriers.
     */
    private List<Solver> solverList = new ArrayList<>();

    /**
     * The list indicating whether the computed tours are optimal.
     */
    private List<Boolean> isOptimalList = new ArrayList<>();

    /**
     * Constructs a new {@code Round} instance.
     */
    public Round() {
    }

    /**
     * Initializes the round with the specified number of couriers and a plan.
     *
     * <p>
     * This method sets up the couriers based on the provided quantity and assigns
     * the given plan to the round. It clears any existing courier and tour
     * assignments to ensure a fresh start.
     * </p>
     *
     * @param CourierQuantity the number of couriers to initialize
     * @param plan            the {@code Plan} object containing intersections and
     *                        sections
     */
    public void init(Integer CourierQuantity, Plan plan) {
        System.out.println("Number of couriers given in the round: " + CourierQuantity);
        courierList.clear();
        for (int i = 0; i < CourierQuantity; i++) {
            courierList.add(new Courier(i));
        }
        this.plan = plan;
        this.tourAttribution = new ArrayList<>();
    }

    /**
     * Soft resets the round by clearing the K-Means clustering instance and
     * resetting the tour attributions.
     *
     * <p>
     * This method allows for re-computation of delivery tours without altering
     * the loaded delivery requests or couriers.
     * </p>
     */
    public void softReset() {
        KNN = new KMeansClustering();
        tourAttribution = new ArrayList<>();
    }

    /**
     * Retrieves the list of delivery tours assigned to couriers.
     *
     * <p>
     * This method prints the routes of each delivery tour for debugging
     * purposes and returns the current list of tour attributions.
     * </p>
     *
     * @return a {@code List} of {@code DeliveryTour} objects representing the
     *         courier assignments
     */
    public List<DeliveryTour> getTourAttribution() {
        for (DeliveryTour tour : tourAttribution) {
            System.out.println("route : " + tour.getRoute());
        }
        return tourAttribution;
    }

    /**
     * Sets the list of delivery tours assigned to couriers.
     *
     * @param tourAttribution the {@code List} of {@code DeliveryTour} objects to be
     *                        assigned
     */
    public void setTourAttribution(List<DeliveryTour> tourAttribution) {
        this.tourAttribution = tourAttribution;
    }

    /**
     * Computes and assigns delivery tours to couriers.
     *
     * <p>
     * This method uses a solver to compute the best route for each courier based
     * on the delivery requests. It distributes delivery points among couriers,
     * solves for optimal routes, and assigns the resulting tours to each courier.
     * </p>
     */
    public void computeRound() {
        // Clone the list to avoid modifying the original during iteration
        List<DeliveryRequest> remainingDeliveries = new ArrayList<>(deliveryRequestList);

        // Determine base number of deliveries per courier and any extras
        int baseDeliveriesPerCourier = remainingDeliveries.size() / courierList.size();
        int extraDeliveries = remainingDeliveries.size() % courierList.size();
        int currentIndex = 0;

        for (Courier courier : courierList) {
            List<Integer> courierDeliveryIndices = new ArrayList<>();
            courierDeliveryIndices.add(plan.getIndexById(warehouse.getId()));

            // Calculate number of deliveries for this courier
            int deliveriesForThisCourier = baseDeliveriesPerCourier + (extraDeliveries > 0 ? 1 : 0);

            // Assign deliveries to this courier
            for (int i = 0; i < deliveriesForThisCourier && currentIndex < remainingDeliveries.size(); i++) {
                DeliveryRequest delivery = remainingDeliveries.get(currentIndex);
                courierDeliveryIndices.add(plan.getIndexById(delivery.getDeliveryAdress().getId()));
                currentIndex++;
            }

            // Initialize and solve the delivery tour for this courier
            System.out.println("Courier " + courier.getId() + " is assigned " + courierDeliveryIndices.size() + " deliveries.");
            Solver solver = new Solver(plan, courierDeliveryIndices, new BnBStrategy()).init();
            solver.solve();
            solver.computePointsToBeServed();
            isOptimalList.add(solver.getTimeExceeded());
            double bestCost = solver.getBestPossibleCost();
            double bestTime = bestCost / (COURIER_SPEED * 1000) * 3600; // Convert to seconds
            LocalTime endTime = LocalTime.of(8, 0).plusSeconds((long) bestTime);

            // Create delivery requests for this courier
            List<DeliveryRequest> courierDeliveryRequests = new ArrayList<>();
            for (Integer requestIndex : courierDeliveryIndices) {
                DeliveryRequest deliveryRequest = new DeliveryRequest(plan.getIntersectionById(plan.getIdByIndex(requestIndex)));
                deliveryRequest.setCourier(courier);
                courierDeliveryRequests.add(deliveryRequest);
            }

            // Retrieve the best route
            List<Integer> bestRouteIndexes = solver.getBestPossiblePath();
            List<Intersection> bestRoute = new ArrayList<>();
            for (Integer index : bestRouteIndexes) {
                bestRoute.add(plan.getIntersectionById(plan.getIdByIndex(index)));
            }

            // Map arrival times to intersections
            Map<Integer, LocalTime> arrivalTimesByIndex = solver.getPointsWithTime();
            Map<Intersection, LocalTime> arrivalTimes = new HashMap<>();
            for (Map.Entry<Integer, LocalTime> entry : arrivalTimesByIndex.entrySet()) {
                arrivalTimes.put(plan.getIntersectionById(plan.getIdByIndex(entry.getKey())), entry.getValue());
            }

            // Create and assign the delivery tour
            DeliveryTour courierDeliveryTour = new DeliveryTour(courier, endTime, courierDeliveryRequests, new ArrayList<>(bestRoute),
                    arrivalTimes);

            tourAttribution.add(courierDeliveryTour);

            // Decrement extra deliveries count
            extraDeliveries--;
        }
    }

    /**
     * Loads delivery requests from an XML file.
     *
     * <p>
     * This method accepts either a file path or a {@code MultipartFile} containing
     * the XML data. It parses the XML to extract warehouse and delivery request
     * information and populates the corresponding lists.
     * </p>
     *
     * @param source either a file path (String) or a {@code MultipartFile} containing
     *               the XML data
     * @throws Exception if the file cannot be found or parsed, or if delivery
     *                   addresses are invalid
     */
    public void loadRequests(Object source) throws Exception {
        deliveryRequestList.clear();
        warehouse = null;
        File xmlFile = null;
        boolean isTemporaryFile = false; // Indicates if the file is temporary

        try {
            // Determine the type of source and process accordingly
            if (source instanceof String) {
                xmlFile = verifyFileExists((String) source);
            } else if (source instanceof MultipartFile) {
                xmlFile = createTemporaryFile((MultipartFile) source);
                isTemporaryFile = true; // Mark as temporary
            } else {
                throw new IllegalArgumentException("Invalid source type.");
            }

            // Parse the XML file
            Document document = parseXmlFile(xmlFile);

            // Load warehouse and delivery requests from the XML
            loadWarehouse(document);
            loadDeliveryRequests(document);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (SAXException e) {
            throw new Exception("Malformed XML file: " + e.getMessage());
        } catch (InstanceNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            throw e;
        } finally {
            // Clean up temporary files if any
            if (isTemporaryFile && xmlFile != null && xmlFile.exists()) {
                xmlFile.delete();
            }
        }
    }

    /**
     * Verifies if the file at the specified path exists.
     *
     * @param filePath the path to the file
     * @return the {@code File} object if the file exists
     * @throws FileNotFoundException if the file does not exist
     */
    private File verifyFileExists(String filePath) throws FileNotFoundException {
        File xmlFile = new File(filePath);
        if (!xmlFile.exists()) {
            throw new FileNotFoundException("The file '" + filePath + "' is not found.");
        }
        return xmlFile;
    }

    /**
     * Creates a temporary file from the uploaded {@code MultipartFile}.
     *
     * @param file the {@code MultipartFile} containing the XML data
     * @return the created temporary {@code File}
     * @throws IOException if the file cannot be created or transferred
     */
    private File createTemporaryFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("tempFile", ".xml");
        file.transferTo(tempFile);
        return tempFile;
    }

    /**
     * Parses an XML file into a {@code Document}.
     *
     * @param xmlFile the XML file to parse
     * @return the parsed {@code Document} object
     * @throws Exception if the XML cannot be parsed
     */
    private Document parseXmlFile(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFile);
    }

    /**
     * Loads the warehouse information from the XML document.
     *
     * @param document the XML document containing the warehouse data
     * @throws InstanceNotFoundException if the warehouse address does not exist in
     *                                   the plan
     * @throws NoSuchElementException    if no warehouse is found in the XML
     */
    private void loadWarehouse(Document document) throws InstanceNotFoundException, NoSuchElementException {
        NodeList warehouseElements = document.getElementsByTagName("entrepot");
        if (warehouseElements.getLength() == 0) {
            throw new NoSuchElementException("No warehouse found in the file.");
        }

        Element warehouseElement = (Element) warehouseElements.item(0);
        String warehouseAddress = warehouseElement.getAttribute("adresse");
        Intersection warehouseIntersection = plan.getIntersectionById(warehouseAddress);
        if (warehouseIntersection == null) {
            throw new InstanceNotFoundException("The warehouse intersection '" + warehouseAddress + "' doesn't exist!");
        }

        warehouse = warehouseIntersection;
    }

    /**
     * Loads delivery requests from the XML document and adds them to the
     * {@code deliveryRequestList}.
     *
     * @param document the XML document containing the delivery request data
     * @throws InstanceNotFoundException if any delivery address does not exist in
     *                                   the plan
     * @throws NoSuchElementException    if no delivery requests are found in the
     *                                   XML
     */
    private void loadDeliveryRequests(Document document) throws InstanceNotFoundException, NoSuchElementException {
        NodeList requestsElements = document.getElementsByTagName("livraison");
        if (requestsElements.getLength() == 0) {
            throw new NoSuchElementException("No delivery requests found in the file.");
        }

        List<DeliveryRequest> tempDeliveryRequestList = new ArrayList<>();
        for (int i = 0; i < requestsElements.getLength(); i++) {
            Element element = (Element) requestsElements.item(i);
            String deliveryAddress = element.getAttribute("adresseLivraison");

            if (plan == null) {
                throw new InstanceNotFoundException(
                        "No plan loaded. Please load a plan before loading delivery requests.");
            }

            Intersection intersection = plan.getIntersectionById(deliveryAddress);
            if (intersection == null) {
                throw new InstanceNotFoundException("The intersection '" + deliveryAddress + "' doesn't exist!");
            }

            DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
            tempDeliveryRequestList.add(deliveryRequest);
        }

        deliveryRequestList = tempDeliveryRequestList;
    }

    /**
     * Sets up the data array from the current list of delivery requests.
     *
     * @return a two-dimensional array of doubles representing the coordinates of
     *         delivery points
     */
    private double[][] setUpData() {
        List<DeliveryRequest> remainingDeliveries = new ArrayList<>(deliveryRequestList);
        double[][] data = new double[remainingDeliveries.size()][2];
        Integer index = 0;
        for (DeliveryRequest delivery : remainingDeliveries) {
            Intersection intersection = delivery.getDeliveryAdress();
            double xcoord = intersection.getLatitude();
            double ycoord = intersection.getLongitude();
            data[index][0] = xcoord;
            data[index][1] = ycoord;
            index += 1;
        }

        return data;
    }

    /**
     * Converts cluster groups of delivery point indices into their corresponding
     * intersection IDs.
     *
     * @param groups an {@code ArrayList} of {@code ArrayList<Integer>} where each
     *               inner list contains indices of delivery points assigned to a
     *               cluster
     * @return an {@code ArrayList} of {@code ArrayList<String>} containing the
     *         intersection IDs for each cluster
     */
    private ArrayList<ArrayList<String>> getIntersectionGroups(ArrayList<ArrayList<Integer>> groups) {
        List<DeliveryRequest> remainingDeliveries = new ArrayList<>(deliveryRequestList);

        ArrayList<ArrayList<String>> intersectionsClusters = new ArrayList<>();
        Integer index = 0;
        for (ArrayList<Integer> cluster : groups) {
            intersectionsClusters.add(new ArrayList<>());
            for (Integer element : cluster) {
                intersectionsClusters.get(index).add(remainingDeliveries.get(element).getDeliveryAdress().getId());
            }
            index += 1;
        }
        return intersectionsClusters;
    }

    /**
     * Computes an optimized delivery round by clustering delivery points and
     * assigning them to couriers.
     *
     * <p>
     * This method uses K-Means clustering to group delivery points based on their
     * geographical coordinates and assigns each group to a courier. It then
     * computes the optimal delivery tour for each courier using the Branch and
     * Bound strategy.
     * </p>
     *
     * @return an {@code ArrayList} of {@code ArrayList<String>} where each inner
     *         list contains the intersection IDs assigned to a courier
     */
    public ArrayList<ArrayList<String>> computeRoundOptimized() {
        // Reset solvers and tour attributions
        solverList.clear();
        tourAttribution.clear();

        // Prepare data for clustering
        double[][] data = setUpData();
        System.out.println("Number of Couriers in the round: " + courierList.size());
        Integer couriersNumber = courierList.size();

        // Perform K-Means clustering
        ArrayList<ArrayList<Integer>> groups = KNN.predictClusters(data, couriersNumber);
        ArrayList<ArrayList<String>> finalGroups = getIntersectionGroups(groups);

        // Assign clusters to couriers and compute delivery tours
        Integer index = 0;
        for (Courier courier : courierList) {
            List<String> group = finalGroups.get(index);
            List<Integer> courierDeliveryIndices = new ArrayList<>();
            courierDeliveryIndices.add(plan.getIndexById(warehouse.getId()));
            for (String intersectionId : group) {
                courierDeliveryIndices.add(plan.getIndexById(intersectionId));
            }

            // Initialize and solve the delivery tour for this courier
            System.out.println("Courier " + courier.getId() + " is assigned " + courierDeliveryIndices.size() + " deliveries.");
            Solver solver = new Solver(plan, courierDeliveryIndices, new BnBStrategy()).init();
            solver.solve();
            solver.computePointsToBeServed();
            solverList.add(solver);
            double bestCost = solver.getBestPossibleCost();
            double bestTime = bestCost / (COURIER_SPEED * 1000) * 3600; // Convert to seconds

            // Create delivery requests for this courier
            List<DeliveryRequest> courierDeliveryRequests = new ArrayList<>();
            for (Integer requestIndex : courierDeliveryIndices) {
                DeliveryRequest deliveryRequest = new DeliveryRequest(plan.getIntersectionById(plan.getIdByIndex(requestIndex)));
                deliveryRequest.setCourier(courier);
                courierDeliveryRequests.add(deliveryRequest);
            }

            // Retrieve the best route
            List<Integer> bestRouteIndexes = solver.getBestPossiblePath(); // Placeholder comment
            // Convert route indices to intersection objects
            List<Intersection> bestRoute = plan.computeTour(bestRouteIndexes);

            // Map arrival times to intersections
            Map<Integer, LocalTime> arrivalTimesByIndex = solver.getPointsWithTime();
            Map<Intersection, LocalTime> arrivalTimes = new HashMap<>();

            for (Map.Entry<Integer, LocalTime> entry : arrivalTimesByIndex.entrySet()) {
                arrivalTimes.put(plan.getIntersectionById(plan.getIdByIndex(entry.getKey())), entry.getValue());
            }
            LocalTime endTime = arrivalTimesByIndex.get(plan.getIndexById(warehouse.getId()));

            // Create and assign the delivery tour
            DeliveryTour courierDeliveryTour = new DeliveryTour(courier, endTime, courierDeliveryRequests, new ArrayList<>(bestRoute),
                    arrivalTimes);
            tourAttribution.add(courierDeliveryTour);

            index += 1;
        }

        return finalGroups;
    }

    /**
     * Retrieves the warehouse intersection for the round.
     *
     * @return the {@code Intersection} object representing the warehouse
     */
    public Intersection getWarehouse() {
        return warehouse;
    }

    /**
     * Retrieves the plan associated with the round.
     *
     * @return the {@code Plan} object containing intersections and sections
     */
    public Plan getPlan() {
        return plan;
    }

    /**
     * Retrieves the list of couriers participating in the round.
     *
     * @return the {@code List} of {@code Courier} objects
     */
    public List<Courier> getCourierList() {
        return courierList;
    }

    /**
     * Retrieves the list of delivery requests for the round.
     *
     * @return the {@code List} of {@code DeliveryRequest} objects
     */
    public List<DeliveryRequest> getDeliveryRequestList() {
        return deliveryRequestList;
    }

    /**
     * Retrieves the list of intersection IDs for all delivery requests.
     *
     * @return a {@code List} of {@code String} containing intersection IDs
     */
    public List<String> getDeliveryIntersectionsList() {
        List<String> deliveryIntersections = new ArrayList<>();
        for (DeliveryRequest deliveryRequest : deliveryRequestList) {
            deliveryIntersections.add(deliveryRequest.getDeliveryAdress().getId());
        }
        return deliveryIntersections;
    }

    /**
     * Clears all delivery requests from the round.
     */
    public void clearDeliveryRequests() {
        this.deliveryRequestList.clear();
    }

    /**
     * Retrieves a delivery request by its intersection ID.
     *
     * @param deliveryRequestId the ID of the delivery request to retrieve
     * @return the {@code DeliveryRequest} object if found, otherwise {@code null}
     */
    public DeliveryRequest getDeliveryRequestById(String deliveryRequestId) {
        for (DeliveryRequest deliveryRequest : deliveryRequestList) {
            if (deliveryRequestId.trim().equals(deliveryRequest.getDeliveryAdress().getId().trim())) {
                return deliveryRequest;
            }
        }
        return null;
    }

    /**
     * Deletes a delivery request by its intersection ID.
     *
     * @param deliveryRequestId the ID of the delivery request to delete
     * @return {@code true} if the delivery request was successfully deleted,
     *         {@code false} otherwise
     */
    public boolean deleteDeliveryRequest(String deliveryRequestId) {
        System.out.println("Trying to delete delivery request with ID: " + deliveryRequestId);
        DeliveryRequest deliveryRequest = getDeliveryRequestById(deliveryRequestId);
        return deliveryRequestList.remove(deliveryRequest);
    }

    /**
     * Adds a delivery request for the specified intersection ID.
     *
     * <p>
     * This method creates a new {@code DeliveryRequest} associated with the given
     * intersection and adds it to the delivery request list.
     * </p>
     *
     * @param intersectionId the unique identifier of the intersection to add as a
     *                       delivery point
     * @return the newly created {@code DeliveryRequest} associated with the
     *         intersection, or {@code null} if the intersection ID is not found in
     *         the plan's intersection map
     */
    public DeliveryRequest addDeliveryIntersection(String intersectionId) {
        intersectionId = intersectionId.trim();
        Intersection intersection = plan.getIntersectionById(intersectionId);

        if (intersection == null) {
            System.out.println("Intersection not found in intersectionMap for ID: " + intersectionId);
            return null;
        }

        DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
        deliveryRequestList.add(deliveryRequest);
        return deliveryRequest;
    }

    /**
     * Computes a new round by updating the delivery tour for a specified courier.
     *
     * <p>
     * This method updates the delivery tour by adding or deleting an intersection
     * point based on the mode. It recalculates the optimal route for the courier
     * and updates the tour attributions accordingly.
     * </p>
     *
     * @param courierIndex      the index of the courier in the {@code courierList}
     * @param intersectionIndex the index of the intersection to add or delete
     * @param mode              the operation mode: {@code -1} to delete the intersection,
     *                          {@code 1} to add
     * @return a {@code List} of updated {@code DeliveryTour} objects
     * @throws IllegalArgumentException if the courier index or intersection index is
     *                                  invalid, or if attempting to delete a
     *                                  non-existent intersection
     * @throws IllegalStateException    if the solver for the courier is not initialized
     */
    private List<DeliveryTour> ComputeNewRound(Integer courierIndex,
                                               Integer intersectionIndex, int mode) {
        if (courierIndex < 0 || courierIndex >= courierList.size()) {
            throw new IllegalArgumentException("Invalid courier index: " + courierIndex);
        }

        Solver courierSolver = solverList.get(courierIndex);
        if (courierSolver == null) {
            throw new IllegalStateException("Solver not initialized for courier: " + courierIndex);
        }

        System.out.println("Before operation - Path: " + courierSolver.getBestPath());

        try {
            // Update solver path based on mode
            if (mode == -1) {
                courierSolver.deleteDeliveryPoint(intersectionIndex);
            } else if (mode == 1) {
                courierSolver.addDeliveryPoint(intersectionIndex);
            }

            // Recompute points to be served
            courierSolver.computePointsToBeServed();

            // Get updated route information
            List<Integer> bestRouteIndexes = courierSolver.getBestPossiblePath();
            List<Intersection> bestRoute = plan.computeTour(bestRouteIndexes);
            Map<Integer, LocalTime> arrivalTimesByIndex = courierSolver.getPointsWithTime();

            // Convert arrival times to Intersection keys
            Map<Intersection, LocalTime> arrivalTimes = new HashMap<>();
            for (Map.Entry<Integer, LocalTime> entry : arrivalTimesByIndex.entrySet()) {
                Intersection intersection = plan.getIntersectionById(plan.getIdByIndex(entry.getKey()));
                if (intersection != null) {
                    arrivalTimes.put(intersection, entry.getValue());
                }
            }

            // Create updated delivery requests based on the new route
            List<DeliveryRequest> updatedDeliveryRequests = new ArrayList<>();
            for (Integer idx : bestRouteIndexes) {
                Intersection intersection = plan.getIntersectionById(plan.getIdByIndex(idx));
                if (intersection != null) {
                    DeliveryRequest dr = new DeliveryRequest(intersection);
                    dr.setCourier(courierList.get(courierIndex));
                    updatedDeliveryRequests.add(dr);
                }
            }

            // Create new tour with updated information
            DeliveryTour updatedTour = new DeliveryTour(
                    courierList.get(courierIndex),
                    arrivalTimesByIndex.get(bestRouteIndexes.get(bestRouteIndexes.size() - 1)),
                    updatedDeliveryRequests,
                    new ArrayList<>(bestRoute),
                    arrivalTimes
            );

            // Update tour attributions
            List<DeliveryTour> newTours = new ArrayList<>(tourAttribution);
            newTours.set(courierIndex, updatedTour);

            System.out.println("After operation - Updated path: " + courierSolver.getBestPath());
            return newTours;
        } catch (Exception e) {
            System.err.println("Error in ComputeNewRound: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Updates the delivery tour for a specified courier by adding or deleting an
     * intersection point.
     *
     * <p>
     * This method recalculates the delivery tour for the specified courier by
     * adding or removing a delivery point. It updates the tour attributions to
     * reflect the changes.
     * </p>
     *
     * @param courierIndex   the index of the courier in the {@code courierList}
     * @param intersectionId the ID of the intersection to add or delete
     * @param mode           the operation mode: {@code -1} to delete the intersection,
     *                       {@code 1} to add
     * @return a {@code List} of updated {@code DeliveryTour} objects
     * @throws IllegalArgumentException if the courier index is invalid
     * @throws IllegalStateException    if an error occurs during the update process
     */
    public List<DeliveryTour> updateLocalPoint(Integer courierIndex, String intersectionId, int mode) {
        System.out.println("Updating local point - Courier: " + courierIndex +
                ", Intersection: " + intersectionId + ", Mode: " + mode);

        try {
            // Reset state to ensure clean computation
            plan.softResetMap();
            plan.preprocessData();

            // Re-compute with updated state
            List<DeliveryTour> result = ComputeNewRound(courierIndex, plan.getIndexById(intersectionId), mode);

            // Update the tourAttribution field
            this.setTourAttribution(result);

            System.out.println("Update completed. New tour size: " + result.size());
            return new ArrayList<>(result);
        } catch (Exception e) {
            System.err.println("Error in updateLocalPoint: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Generates a report of the current delivery tours and saves it to a text
     * file.
     *
     * <p>
     * The report includes details such as courier IDs, their assigned delivery
     * points, arrival and departure times, and routes taken. The generated report
     * is returned as a {@code String}.
     * </p>
     *
     * @return a {@code String} containing the formatted delivery tours report
     */
    public String generateTourReport() {
        StringBuilder fileContent = new StringBuilder();
        fileContent.append("DELIVERY TOURS REPORT\n");
        fileContent.append("===================\n\n");

        List<DeliveryTour> tours = getTourAttribution();

        for (DeliveryTour tour : tours) {
            fileContent.append("Courier #").append(tour.getCourier().getId()).append("\n");
            fileContent.append("-------------\n");
            fileContent.append("Starting from warehouse at: 08:00\n\n");

            List<Intersection> route = tour.getRoute();
            Map<Intersection, LocalTime> arrivalTimes = tour.getArrivalTimes();
            List<DeliveryRequest> sortedDeliveryRequests = tour.getSortedDeliveryRequests();
            int sectionCounter = 1;

            String currentStreetName = null;
            double accumulatedDistance = 0;

            for (int i = 0; i < route.size() - 3; i++) {
                Intersection currentIntersection = route.get(i);
                Intersection nextIntersection = route.get(i + 1);

                Section currentSection = plan.getSections().stream()
                        .filter(s -> (s.getOrigin().equals(currentIntersection.getId()) &&
                                s.getDestination().equals(nextIntersection.getId())) ||
                                (s.getDestination().equals(currentIntersection.getId()) &&
                                        s.getOrigin().equals(nextIntersection.getId())))
                        .findFirst()
                        .orElse(null);

                if (currentSection != null) {
                    String streetName = currentSection.getName().trim();
                    if (streetName.isEmpty()) {
                        streetName = "Undefined street";
                    }

                    if (streetName.equals(currentStreetName)) {
                        accumulatedDistance += currentSection.getLength();
                    } else {
                        if (currentStreetName != null) {
                            fileContent.append(String.format("%d. %s (%.2f m)\n",
                                    sectionCounter++, currentStreetName, accumulatedDistance));
                        }
                        currentStreetName = streetName;
                        accumulatedDistance = currentSection.getLength();
                    }
                }

                boolean isDeliveryPoint = sortedDeliveryRequests.stream()
                        .anyMatch(dr -> dr.getDeliveryAdress().getId().equals(nextIntersection.getId()));

                if (isDeliveryPoint) {
                    if (currentStreetName != null) {
                        fileContent.append(String.format("%d. %s (%.2f m)\n",
                                sectionCounter++, currentStreetName, accumulatedDistance));
                    }

                    LocalTime arrivalTime = arrivalTimes.get(nextIntersection);
                    if (arrivalTime != null) {
                        fileContent.append("\n   >>> Delivery Point for ");
                        fileContent.append(String.format("%s <<<\n", currentStreetName));
                        fileContent.append(String.format("   Arrival: %s\n", arrivalTime));
                        LocalTime departureTime = arrivalTime.plusMinutes(5);
                        fileContent.append(String.format("   Departure: %s\n", departureTime));
                        fileContent.append("\n");
                    }

                    currentStreetName = null;
                    accumulatedDistance = 0;
                }
            }

            if (currentStreetName != null && accumulatedDistance > 0) {
                fileContent.append(String.format("%d. %s (%.2f m)\n",
                        sectionCounter, currentStreetName, accumulatedDistance));
            }

            fileContent.append("\nReturn to warehouse at: ")
                    .append(arrivalTimes.get(route.get(route.size() - 2)))
                    .append("\n\n");
        }

        return fileContent.toString();
    }

    /**
     * Defines the warehouse location by setting it to the specified intersection
     * ID.
     *
     * <p>
     * This method updates the warehouse location for the round. If the provided
     * intersection ID does not exist in the plan's intersection map, the method
     * returns {@code null}.
     * </p>
     *
     * @param intersectionId the unique identifier of the intersection to set as
     *                       warehouse
     * @return the {@code Intersection} object representing the new warehouse, or
     *         {@code null} if the intersection ID is invalid
     */
    public Intersection defineWarehousePoint(String intersectionId) {
        intersectionId = intersectionId.trim();
        Intersection intersection = plan.getIntersectionById(intersectionId);

        if (intersection == null) {
            System.out.println("Intersection not found in intersectionMap for ID: " + intersectionId);
            return null;
        }

        warehouse = intersection;
        return warehouse;
    }

    /**
     * Deletes the current warehouse location.
     *
     * <p>
     * This method removes the warehouse assignment from the round by setting it
     * to {@code null}.
     * </p>
     */
    public void deleteWarehouse() {
        System.out.println("Trying to delete the warehouse");
        warehouse = null;
    }

    /**
     * Retrieves the list indicating whether each courier's delivery tour is
     * optimal.
     *
     * @return a {@code List} of {@code Boolean} values where each element corresponds
     *         to a courier's tour optimization status
     */
    public List<Boolean> getIsOptimalList() {
        return this.isOptimalList;
    }
}
