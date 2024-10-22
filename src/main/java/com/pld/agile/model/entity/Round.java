package com.pld.agile.model.entity;

import com.pld.agile.model.strategy.BnBStrategy;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.Solver;
import com.pld.agile.model.strategy.TspStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.InstanceNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;

public class Round {
    private static final double COURIER_SPEED = 15.0; // km/h

    private Plan plan;
    private List<Courier> courierList=new ArrayList<>();
    private List<DeliveryRequest> deliveryRequestList=new ArrayList<>();
    private Map<Courier, DeliveryTour> tourAttribution= new HashMap<>();

    public Round() {}

    public void init(Integer CourierQuantity, Plan plan) {
        for (int i = 0; i< CourierQuantity; i++) {
            courierList.add(new Courier(i));
        }
        this.plan = plan;
        this.plan.reIndexIntersections();
        this.plan.makeCostsMatrix();
        this.deliveryRequestList = new ArrayList<DeliveryRequest>();
        this.tourAttribution = new HashMap<Courier, DeliveryTour>();
    }

    public Map<Courier, DeliveryTour> getTourAttribution() {
        return tourAttribution;
    }

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

    public Plan getPlan() {
        return plan;
    }

    public List<Courier> getCourierList() {
        return courierList;
    }

    public List<DeliveryRequest> getDeliveryRequestList() {
        return deliveryRequestList;
    }
}
