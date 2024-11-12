package com.pld.agile.model.entity;

import com.pld.agile.model.strategy.BnBStrategy;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.Solver;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.InstanceNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.time.LocalTime;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.pld.agile.model.algorithm.KMeansClustering;

/**
 * The {@code Round} class represents a round of deliveries managed by a fleet
 * of couriers.
 * It manages the assignment of delivery requests to couriers, computes delivery
 * tours,
 * and tracks the delivery information.
 */
public class Round {
    private static final double COURIER_SPEED = 15.0; // km/h

    private Plan plan;
    private List<Courier> courierList = new ArrayList<>();
    private List<DeliveryRequest> deliveryRequestList = new ArrayList<>();
    private List<DeliveryTour> tourAttribution = new ArrayList<>();
    private Intersection warehouse;
    private KMeansClustering KNN = new KMeansClustering();
    private List<Solver> solverList = new ArrayList<>();

    public Round() {
    }

    /**
     * Initializes the round with the specified number of couriers and a plan.
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
        this.tourAttribution = new ArrayList<DeliveryTour>();
    }

    /**
     * Returns the map of courier assignments to delivery tours.
     *
     * @return a map where each {@code Courier} is assigned a {@code DeliveryTour}
     */
    public List<DeliveryTour> getTourAttribution() {
        return tourAttribution;
    }

    /**
     * Computes and assigns delivery tours to couriers. This method uses a solver to
     * compute
     * the best route for each courier based on the delivery requests.
     */
    public void computeRound() {
        // TODO
        // While the Delivery Request list isn't empty
        // we will launch the graph calculation, create a DeliveryTour ith the result,
        // assign it to a courier and update TourAttribution, then delete the
        // DeliveryRequest we used from the list
        List<Integer> indexedID = new ArrayList<Integer>();

        List<DeliveryRequest> remainingDeliveries = new ArrayList<>(deliveryRequestList);

        int baseDeliveriesPerCourier = remainingDeliveries.size() / courierList.size();
        int extraDeliveries = remainingDeliveries.size() % courierList.size();
        int currentIndex = 0;

        for (Courier courier : courierList) {
            List<Integer> courierDeliveryIndices = new ArrayList<>();
            courierDeliveryIndices.add(plan.getIndexById(warehouse.getId()));

            // Number of deliveries for this Courier
            int deliveriesForThisCourier = baseDeliveriesPerCourier + (extraDeliveries > 0 ? 1 : 0);

            // Delivery attribution
            for (int i = 0; i < deliveriesForThisCourier && currentIndex < remainingDeliveries.size(); i++) {
                DeliveryRequest delivery = remainingDeliveries.get(currentIndex);
                courierDeliveryIndices.add(plan.getIndexById(delivery.getDeliveryAdress().getId()));
                currentIndex++;
            }

            // Solve the courier tour : To keep after change of code (creation of delivery
            // tour)
            System.out.println(
                    "Courier " + courier.getId() + " is assigned " + courierDeliveryIndices.size() + " deliveries.");
            Solver solver = new Solver(plan, courierDeliveryIndices, new BnBStrategy()).init();
            solver.solve();
            solver.computePointsToBeServed();

            double bestCost = solver.getBestPossibleCost();
            double bestTime = bestCost / (COURIER_SPEED * 1000) * 3600; // In seconds
            LocalTime endTime = LocalTime.of(8, 0).plusSeconds((long) bestTime);

            List<DeliveryRequest> courierDeliveryRequests = new ArrayList<>();
            for (Integer requestIndex : courierDeliveryIndices) {
                DeliveryRequest deliveryRequest = new DeliveryRequest(
                        plan.getIntersectionById(plan.getIdByIndex(requestIndex)));
                deliveryRequest.setCourier(courier);
                courierDeliveryRequests.add(deliveryRequest);
            }

            // TODO remplir ceci avec les résultats du GPS
            Integer warehouseIndex = plan.getIndexById(warehouse.getId());
            List<Integer> bestRouteIndexes = solver.getBestPossiblePath(); // jsp
            List<Intersection> bestRoute = new ArrayList<>();
            for (Integer index : bestRouteIndexes) {
                bestRoute.add(plan.getIntersectionById(plan.getIdByIndex(index)));
            }

            Map<Integer, LocalTime> arrivalTimesByIndex = solver.getPointsWithTime();
            Map<Intersection, LocalTime> arrivalTimes = new HashMap<>();
            for (Map.Entry<Integer, LocalTime> entry : arrivalTimesByIndex.entrySet()) {
                arrivalTimes.put(plan.getIntersectionById(plan.getIdByIndex(entry.getKey())), entry.getValue());
            }

            DeliveryTour courierDeliveryTour = new DeliveryTour(courier, endTime, courierDeliveryRequests, bestRoute,
                    arrivalTimes);

            tourAttribution.add(courierDeliveryTour);

            extraDeliveries--;
        }
    }

    // /**
    // * Loads delivery requests from an XML file.
    // *
    // * @param filePath the path to the XML file
    // * @throws Exception if the file cannot be found or parsed, or if delivery
    // * addresses are invalid
    // */
    // public void loadRequests(String filePath) throws Exception {
    // try {
    // File xmlFile = new File(filePath);
    // // Verifying if the file exists
    // if (!xmlFile.exists()) {
    // throw new FileNotFoundException("The file '" + filePath + "' is not found.");
    // }

    // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // DocumentBuilder builder = factory.newDocumentBuilder();
    // Document document = builder.parse(xmlFile);

    // // Reading the Requests
    // NodeList requestsElements = document.getElementsByTagName("livraison");
    // for (int i = 0; i < requestsElements.getLength(); i++) {
    // Element element = (Element) requestsElements.item(i);
    // String deliveryAdress = element.getAttribute("adresseLivraison");

    // // Create the DeliveryRequest Object
    // Intersection intersection = plan.getIntersectionById(deliveryAdress);
    // if (intersection == null) {
    // throw new InstanceNotFoundException("The intersection '" + deliveryAdress +
    // "' doesn't exist !");
    // }
    // DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
    // deliveryRequestList.add(deliveryRequest);
    // }
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // throw e; // Propagate exception if file not found
    // } catch (SAXException e) {
    // // Captures errors related to malformed XML parsing
    // throw new Exception("Malformed XML file : : " + e.getMessage());
    // } catch (InstanceNotFoundException e) {
    // e.printStackTrace();
    // throw e;
    // }
    // }

    // /**
    // * Loads delivery requests from an XML file.
    // *
    // * @param file the XML file as a MultipartFile
    // * @throws Exception if the file cannot be found or parsed, or if delivery
    // * addresses are invalid, or if there was no plan loaded
    // */

    // /**
    // * Loads delivery requests from an XML file.
    // *
    // * @param file the XML file as a MultipartFile
    // * @throws Exception if the file cannot be found or parsed, or if delivery
    // * addresses are invalid, or if there was no plan loaded
    // */
    // public void loadRequestsByfile(MultipartFile file) throws Exception {
    // File xmlFile = null;
    // try {
    // xmlFile = createTemporaryFile(file);

    // Document document = parseXmlFile(xmlFile);
    // loadWarehouse(document);
    // loadDeliveryRequests(document);

    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // throw e;
    // } catch (SAXException e) {
    // throw new Exception("Malformed XML file: " + e.getMessage());
    // } catch (InstanceNotFoundException | NoSuchElementException e) {
    // e.printStackTrace();
    // throw e;
    // } finally {
    // if (xmlFile != null && xmlFile.exists()) {
    // xmlFile.delete();
    // }
    // }
    // }

    // /**
    // * Creates a temporary file from the uploaded MultipartFile.
    // *
    // * @param file the MultipartFile containing the XML data
    // * @return the created temporary File
    // * @throws IOException if the file cannot be created or transferred
    // */
    // private File createTemporaryFile(MultipartFile file) throws IOException {
    // File tempFile = File.createTempFile("tempFile", ".xml");
    // file.transferTo(tempFile);
    // return tempFile;
    // }

    // /**
    // * Parses an XML file into a Document.
    // *
    // * @param xmlFile the XML file to parse
    // * @return the parsed Document object
    // * @throws Exception if the XML cannot be parsed
    // */
    // private Document parseXmlFile(File xmlFile) throws Exception {
    // DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // DocumentBuilder builder = factory.newDocumentBuilder();
    // return builder.parse(xmlFile);
    // }

    // /**
    // * Loads the warehouse information from the XML document.
    // *
    // * @param document the XML document containing the warehouse data
    // * @throws InstanceNotFoundException if the warehouse address does not exist
    // in
    // * the plan
    // * @throws NoSuchElementException if no warehouse is found in the XML
    // */
    // private void loadWarehouse(Document document) throws
    // InstanceNotFoundException, NoSuchElementException {
    // NodeList warehouseElements = document.getElementsByTagName("entrepot");
    // if (warehouseElements.getLength() == 0) {
    // throw new NoSuchElementException("No warehouse found in the file.");
    // }

    // Element warehouseElement = (Element) warehouseElements.item(0);
    // String warehouseAddress = warehouseElement.getAttribute("adresse");
    // Intersection warehouseIntersection =
    // plan.getIntersectionById(warehouseAddress);
    // if (warehouseIntersection == null) {
    // throw new InstanceNotFoundException("The warehouse intersection '" +
    // warehouseAddress + "' doesn't exist!");
    // }

    // warehouse = warehouseIntersection;
    // }

    // /**
    // * Loads delivery requests from the XML document and adds them to the
    // * deliveryRequestList.
    // *
    // * @param document the XML document containing the delivery request data
    // * @throws InstanceNotFoundException if any delivery address does not exist in
    // * the plan
    // * @throws NoSuchElementException if no delivery requests are found in the
    // * XML
    // */
    // private void loadDeliveryRequests(Document document) throws
    // InstanceNotFoundException, NoSuchElementException {
    // NodeList requestsElements = document.getElementsByTagName("livraison");
    // if (requestsElements.getLength() == 0) {
    // throw new NoSuchElementException("No delivery requests found in the file.");
    // }

    // List<DeliveryRequest> tempDeliveryRequestList = new ArrayList<>();
    // for (int i = 0; i < requestsElements.getLength(); i++) {
    // Element element = (Element) requestsElements.item(i);
    // String deliveryAddress = element.getAttribute("adresseLivraison");

    // if (plan == null) {
    // throw new InstanceNotFoundException(
    // "No plan loaded. Please load a plan before loading delivery requests.");
    // }

    // Intersection intersection = plan.getIntersectionById(deliveryAddress);
    // if (intersection == null) {
    // throw new InstanceNotFoundException("The intersection '" + deliveryAddress +
    // "' doesn't exist!");
    // }

    // DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
    // tempDeliveryRequestList.add(deliveryRequest);
    // }

    // deliveryRequestList = tempDeliveryRequestList;
    // }

    /**
     * Loads delivery requests from an XML file given either a file path or a
     * MultipartFile.
     *
     * @param source either a file path (String) or a MultipartFile containing the
     *               XML data
     * @throws Exception if the file cannot be found or parsed, or if delivery
     *                   addresses are invalid
     */
    public void loadRequests(Object source) throws Exception {
        File xmlFile = null;
        try {
            // Check if source is a file path or a MultipartFile
            xmlFile = (source instanceof String) ? verifyFileExists((String) source)
                    : createTemporaryFile((MultipartFile) source);

            Document document = parseXmlFile(xmlFile);

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
            if (xmlFile != null && xmlFile.exists()) {
                xmlFile.delete();
            }
        }
    }

    /**
     * Verifies if the file at the specified path exists.
     *
     * @param filePath the path to the file
     * @return the File object if the file exists
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
     * Creates a temporary file from the uploaded MultipartFile.
     *
     * @param file the MultipartFile containing the XML data
     * @return the created temporary File
     * @throws IOException if the file cannot be created or transferred
     */
    private File createTemporaryFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("tempFile", ".xml");
        file.transferTo(tempFile);
        return tempFile;
    }

    /**
     * Parses an XML file into a Document.
     *
     * @param xmlFile the XML file to parse
     * @return the parsed Document object
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
     * deliveryRequestList.
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

    public ArrayList<ArrayList<String>> computeRoundOptimized() {

        // Definition of the groups of intersections to assign Couriers
        double[][] data = setUpData();
        System.out.println("Number of Couriers in the round: " + courierList.size());
        Integer couriersNumber = courierList.size();
        ArrayList<ArrayList<Integer>> groups = KNN.predictClusters(data, couriersNumber);
        ArrayList<ArrayList<String>> finalGroups = getIntersectionGroups(groups);

        // Assigning the groups to the couriers
        Integer index = 0;
        for (Courier courier : courierList) {
            List<String> group = finalGroups.get(index);
            List<Integer> courierDeliveryIndices = new ArrayList<>();
            courierDeliveryIndices.add(plan.getIndexById(warehouse.getId()));
            for (String intersectionId : group) {
                courierDeliveryIndices.add(plan.getIndexById(intersectionId));
            }

            // Solve the courier tour : To keep after change of code (creation of delivery
            // tour)
            System.out.println(
                    "Courier " + courier.getId() + " is assigned " + courierDeliveryIndices.size() + " deliveries.");
            Solver solver = new Solver(plan, courierDeliveryIndices, new BnBStrategy()).init();
            solver.solve();
            solver.computePointsToBeServed();
            solverList.add(solver);
            double bestCost = solver.getBestPossibleCost();
            double bestTime = bestCost / (COURIER_SPEED * 1000) * 3600; // In seconds

            List<DeliveryRequest> courierDeliveryRequests = new ArrayList<>();
            for (Integer requestIndex : courierDeliveryIndices) {
                DeliveryRequest deliveryRequest = new DeliveryRequest(
                        plan.getIntersectionById(plan.getIdByIndex(requestIndex)));
                deliveryRequest.setCourier(courier);
                courierDeliveryRequests.add(deliveryRequest);
            }

            // TODO remplir ceci avec les résultats du GPS
            Integer warehouseIndex = plan.getIndexById(warehouse.getId());
            List<Integer> bestRouteIndexes = solver.getBestPossiblePath(); // jsp
            // Turning the path between delivery points into a global path with all
            // intersections
            List<Intersection> bestRoute = plan.computeTour(bestRouteIndexes);

            //
            // List<Intersection> bestRoute = new ArrayList<>(); // Might need to turn that
            // into a String and only keep the
            // // ID
            // bestRoute.add(warehouse);
            // for (Integer i : bestRouteIndexes) {
            // bestRoute.add(plan.getIntersectionById(plan.getIdByIndex(i)));
            // }
            // bestRoute.add(warehouse);

            Map<Integer, LocalTime> arrivalTimesByIndex = solver.getPointsWithTime();
            Map<Intersection, LocalTime> arrivalTimes = new HashMap<>(); // Might need to turn that into a String and
                                                                         // only keep the ID

            for (Map.Entry<Integer, LocalTime> entry : arrivalTimesByIndex.entrySet()) {
                arrivalTimes.put(plan.getIntersectionById(plan.getIdByIndex(entry.getKey())), entry.getValue());
            }
            LocalTime endTime = arrivalTimesByIndex.get(warehouseIndex); // TODO doesnt seem to work well, maybe
                                                                         // warehouseIndex is not the right index or
                                                                         // solver does not treat him first

            DeliveryTour courierDeliveryTour = new DeliveryTour(courier, endTime, courierDeliveryRequests, bestRoute,
                    arrivalTimes);
            tourAttribution.add(courierDeliveryTour);

            index += 1;
        }

        return finalGroups;
    }

    public Intersection getWarehouse() {
        return warehouse;
    }

    public Plan getPlan() {
        return plan;
    }

    /**
     * Returns the list of couriers for this round.
     *
     * @return the list of {@code Courier} objects
     */
    public List<Courier> getCourierList() {
        return courierList;
    }

    /**
     * Returns the list of delivery requests for this round.
     *
     * @return the list of {@code DeliveryRequest} objects
     */
    public List<DeliveryRequest> getDeliveryRequestList() {
        return deliveryRequestList;
    }

    public List<String> getDeliveryIntersectionsList() {
        List<String> deliveryIntersections = new ArrayList<>();
        for (DeliveryRequest deliveryRequest : deliveryRequestList) {
            deliveryIntersections.add(deliveryRequest.getDeliveryAdress().getId());
        }
        return deliveryIntersections;
    }

    public void clearDeliveryRequests() {
        this.deliveryRequestList.clear();
    }

    public DeliveryRequest getDeliveryRequestById(String deliveryRequestId) {
        for (DeliveryRequest deliveryRequest : deliveryRequestList) {
            if (deliveryRequestId.trim().equals(deliveryRequest.getDeliveryAdress().getId().trim())) {
                return deliveryRequest;
            }
        }
        return null;
    }

    public boolean deleteDeliveryRequest(String deliveryRequestId) {
        System.out.println("Trying to delete delivery request with ID: " + deliveryRequestId);
        DeliveryRequest deliveryRequest = getDeliveryRequestById(deliveryRequestId);
        return deliveryRequestList.remove(deliveryRequest);
    }

    /**
     * Adds a delivery request for the specified intersection ID.
     * 
     * @param intersectionId The unique identifier of the intersection to add as a
     *                       delivery point.
     * @return DeliveryRequest The newly created delivery request associated with
     *         the intersection. Returns null if the intersection ID is not found in
     *         the plan's intersection map.
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
     * Updates the delivery tour for a specified courier by adding or deleting an
     * intersection point.
     *
     * @param courierIndex      The index of the courier in the courierList.
     * @param intersectionIndex The index of the intersection to add or delete.
     * @param mode              The operation mode: -1 to delete the intersection, 1
     *                          to add.
     * @throws IllegalArgumentException if the courier index is invalid, the
     *                                  intersection index is invalid,
     *                                  or if attempting to delete a non-existent
     *                                  intersection.
     */
    public List<DeliveryTour> ComputeNewRound(Integer courierIndex, Integer intersectionIndex, int mode) {
        // Validate courier index
        if (courierIndex < 0 || courierIndex >= courierList.size()) {
            throw new IllegalArgumentException("Invalid courier index.");
        }

        // Retrieve the Solver for the specified courier
        Solver courierSolver = solverList.get(courierIndex);
        if (courierSolver == null) {
            throw new IllegalStateException("Solver not initialized for the specified courier.");
        }

        // Perform add or delete operation
        if (mode == -1) { // Delete operation
            try {
                System.out.println(" choose from here :" + courierSolver.getBestPath());
                courierSolver.deleteDeliveryPoint(intersectionIndex);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to delete intersection: " + e.getMessage());
            }
        } else if (mode == 1) { // Add operation
            try {
                courierSolver.addDeliveryPoint(intersectionIndex);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to add intersection: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid mode. Use -1 to delete or 1 to add.");
        }

        // Recompute points to be served
        courierSolver.computePointsToBeServed();

        // Retrieve updated best path and other details
        double bestCost = courierSolver.getBestPossibleCost();
        double bestTime = bestCost / (COURIER_SPEED * 1000) * 3600; // Convert to seconds

        List<Integer> bestRouteIndexes = courierSolver.getBestPossiblePath();
        List<Intersection> bestRoute = plan.computeTour(bestRouteIndexes);

        // Retrieve arrival times and map them to Intersection objects
        Map<Integer, LocalTime> arrivalTimesByIndex = courierSolver.getPointsWithTime();
        Map<Intersection, LocalTime> arrivalTimes = new HashMap<>();
        for (Map.Entry<Integer, LocalTime> entry : arrivalTimesByIndex.entrySet()) {
            Intersection intersectionObj = plan.getIntersectionById(plan.getIdByIndex(entry.getKey()));
            if (intersectionObj != null) {
                arrivalTimes.put(intersectionObj, entry.getValue());
            }
        }

        // Determine end time based on warehouse index
        Integer warehouseIndex = plan.getIndexById(warehouse.getId());
        LocalTime endTime = arrivalTimesByIndex.getOrDefault(warehouseIndex, LocalTime.of(8, 0));

        // Update DeliveryRequest list based on the updated bestRouteIndexes
        List<DeliveryRequest> updatedDeliveryRequests = new ArrayList<>();
        for (Integer idx : bestRouteIndexes) {
            Intersection intersection = plan.getIntersectionById(plan.getIdByIndex(idx));
            if (intersection != null) {
                DeliveryRequest dr = new DeliveryRequest(intersection);
                dr.setCourier(courierList.get(courierIndex));
                updatedDeliveryRequests.add(dr);
            }
        }

        // Create the updated DeliveryTour object
        DeliveryTour updatedTour = new DeliveryTour(
                courierList.get(courierIndex),
                endTime,
                updatedDeliveryRequests,
                bestRoute,
                arrivalTimes);

        // Replace the existing DeliveryTour
        tourAttribution.set(courierIndex, updatedTour);
        return tourAttribution;
    }

}
