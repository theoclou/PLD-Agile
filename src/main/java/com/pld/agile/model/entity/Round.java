package com.pld.agile.model.entity;

import com.pld.agile.model.strategy.BnBStrategy;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.Solver;
import com.pld.agile.model.strategy.TspStrategy;
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
/**
 * The {@code Round} class represents a round of deliveries managed by a fleet of couriers.
 * It manages the assignment of delivery requests to couriers, computes delivery tours,
 * and tracks the delivery information.
 */
public class Round {
    private static final double COURIER_SPEED = 15.0; // km/h

    private Plan plan;
    private List<Courier> courierList=new ArrayList<>();
    private List<DeliveryRequest> deliveryRequestList=new ArrayList<>();
    private Map<Courier, DeliveryTour> tourAttribution= new HashMap<>();
    private Intersection warehouse;

    public Round() {}
    /**
     * Initializes the round with the specified number of couriers and a plan.
     *
     * @param CourierQuantity the number of couriers to initialize
     * @param plan            the {@code Plan} object containing intersections and sections
     */
    public void init(Integer CourierQuantity, Plan plan) {
        for (int i = 0; i< CourierQuantity; i++) {
            courierList.add(new Courier(i));
        }
        this.plan = plan;
        this.deliveryRequestList = new ArrayList<DeliveryRequest>();
        this.tourAttribution = new HashMap<Courier, DeliveryTour>();
    }
    /**
     * Returns the map of courier assignments to delivery tours.
     *
     * @return a map where each {@code Courier} is assigned a {@code DeliveryTour}
     */
    public Map<Courier, DeliveryTour> getTourAttribution() {
        return tourAttribution;
    }

    /**
     * Computes and assigns delivery tours to couriers. This method uses a solver to compute
     * the best route for each courier based on the delivery requests.
     */
    public void computeRound(){
        // TODO
        // Tant que la liste de Delivery Request n'est pas vide, on va
        // lancer un calcul de graph, créer un DeliveryTour avec le résultat, l'affecter
        // à un Courier et mettre à jour TourAttribution, puis supprimer les DeliveryRequest qu'on a utilisé
        // de la liste
        List<Integer> indexedID= new ArrayList<Integer>();

        List<DeliveryRequest> remainingDeliveries = new ArrayList<>(deliveryRequestList);

        int baseDeliveriesPerCourier = remainingDeliveries.size() / courierList.size();
        int extraDeliveries = remainingDeliveries.size() % courierList.size();

        int currentIndex = 0;

        for (Courier courier : courierList) {
            List<Integer> courierDeliveryIndices = new ArrayList<>();

            // Number of deliveries for this Courier
            int deliveriesForThisCourier = baseDeliveriesPerCourier + (extraDeliveries > 0 ? 1 : 0);

            //Delivery attribution
            for (int i = 0; i < deliveriesForThisCourier && currentIndex < remainingDeliveries.size(); i++) {
                DeliveryRequest delivery = remainingDeliveries.get(currentIndex);
                courierDeliveryIndices.add(plan.getIndexById(delivery.getDeliveryAdress().getId()));
                currentIndex++;
            }

            //Solve the courier tour
            Solver solver= new Solver(plan, indexedID, new BnBStrategy()).init();
            solver.solve();

            double bestCost = solver.getBestCost();
            double bestTime = bestCost/(COURIER_SPEED * 1000); //In minutes
            LocalTime endTime = LocalTime.of(8,0).plusMinutes((long) bestTime);

            List<DeliveryRequest> courierDeliveryRequests = new ArrayList<>();
            for(Integer requestIndex : courierDeliveryIndices) {
                DeliveryRequest deliveryRequest = new DeliveryRequest(plan.getIntersectionById(plan.getIdByIndex(requestIndex)));
                deliveryRequest.setCourier(courier);
                courierDeliveryRequests.add(deliveryRequest);
            }

            //TODO remplir ceci avec les résultats du GPS
            List<Section> route = new ArrayList<>();

            Map<Intersection, LocalTime> arrivalTimes = new HashMap<>();


            DeliveryTour courierDeliveryTour = new DeliveryTour(courier,endTime, courierDeliveryRequests, route, arrivalTimes);

            extraDeliveries--;
        }
    }
    /**
     * Loads delivery requests from an XML file.
     *
     * @param filePath the path to the XML file
     * @throws Exception if the file cannot be found or parsed, or if delivery addresses are invalid
     */
    public void loadRequests(String filePath) throws Exception {
        try{
            File xmlFile = new File(filePath);
            // Verifying if the file exists
            if (!xmlFile.exists()){
                throw new FileNotFoundException("The file '" + filePath + "' is not found.");
            }


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Reading the Requests
            NodeList requestsElements = document.getElementsByTagName("livraison");
            for(int i = 0; i < requestsElements.getLength(); i++){
                Element element = (Element) requestsElements.item(i);
                String deliveryAdress = element.getAttribute("adresseLivraison");

                // Create the DeliveryRequest Object
                Intersection intersection = plan.getIntersectionById(deliveryAdress);
                if (intersection == null){
                    throw new InstanceNotFoundException("The intersection '" + deliveryAdress + "' doesn't exist !");
                }
                DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
                deliveryRequestList.add(deliveryRequest);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e; // Propagate exception if file not found
        } catch (SAXException e) {
            // Captures errors related to malformed XML parsing
            throw new Exception("Malformed XML file : : " + e.getMessage());
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }
    /**
     * Returns the {@code Plan} object associated with this round.
     *
     * @return the plan
     */

    public void loadRequestsByfile(MultipartFile file) throws Exception {
        File xmlFile = null;
        try{
            xmlFile = File.createTempFile("tempFile", ".xml");
            file.transferTo(xmlFile);


            List<DeliveryRequest> tempDeliveryRequestList = new ArrayList<>();  // Temporary list to avoid modifying the list if an error occurs

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Reading the Warehouse
            NodeList warehouseElements = document.getElementsByTagName("entrepot");
            if (warehouseElements.getLength() == 0){
                throw new NoSuchElementException("No warehouse found in the file.");
            }
            Element warehouseElement = (Element) warehouseElements.item(0);
            String warehouseAdress = warehouseElement.getAttribute("adresse");
            System.out.println(warehouseAdress);
            Intersection warehouseIntersection = plan.getIntersectionById(warehouseAdress);
            if (warehouseIntersection == null){
                throw new InstanceNotFoundException("The warehouse intersection '" + warehouseAdress + "' doesn't exist !");
            }
            warehouse = warehouseIntersection;

            // Reading the Requests
            NodeList requestsElements = document.getElementsByTagName("livraison");
            if (requestsElements.getLength() == 0){
                throw new NoSuchElementException("No delivery requests found in the file.");
            }

            for(int i = 0; i < requestsElements.getLength(); i++){
                Element element = (Element) requestsElements.item(i);
                String deliveryAdress = element.getAttribute("adresseLivraison");

                if (plan == null){
                    throw new InstanceNotFoundException("No plan loaded. Please load a plan before loading delivery requests.");
                }
                // Create the DeliveryRequest Object
                Intersection intersection = plan.getIntersectionById(deliveryAdress);
                if (intersection == null){
                    throw new InstanceNotFoundException("The intersection '" + deliveryAdress + "' doesn't exist !");
                }
                DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
                tempDeliveryRequestList.add(deliveryRequest);
            }
            deliveryRequestList = tempDeliveryRequestList;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e; // Propagate exception if file not found
        } catch (SAXException e) {
            // Captures errors related to malformed XML parsing
            throw new Exception("Malformed XML file : : " + e.getMessage());
        } catch (InstanceNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (xmlFile != null && xmlFile.exists()) {
                xmlFile.delete();
            }
        }
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
        this.deliveryRequestList.clear(); // Assurez-vous que deliveryRequestList est initialisé
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

    public boolean addDeliveryIntersection(String intersectionId) {
        System.out.println("Trying to add delivery point with ID: " + intersectionId);
        Intersection intersection = plan.getIntersectionById(intersectionId);
        DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
        return deliveryRequestList.add(deliveryRequest);
    }
}
