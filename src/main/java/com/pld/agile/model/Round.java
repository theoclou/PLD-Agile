package com.pld.agile.model;

import com.pld.agile.model.tsp.TSP1;
import com.pld.agile.model.tspOptimized.BranchAndBound;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;

public class Round {
    private Plan plan;
    private List<Courier> courierList=new ArrayList<>();
    private List<DeliveryRequest> deliveryRequestList=new ArrayList<>();
    private Map<Courier, DeliveryTour> tourAttribution= new HashMap<>();

    public Round() {}

    public void init(List<Courier> courierList, Plan plan) {
        this.courierList = courierList;
        this.plan = plan;
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
        for (DeliveryRequest deliveryRequest : deliveryRequestList) {
            indexedID.add(Integer.parseInt(deliveryRequest.getDeliveryAdress().getId()));
        }
        //v1
        Solver solverTSP = new Solver(plan, indexedID, new TspStrategy()).init();
        solverTSP.solve();

        //v2
        Solver solverBNB = new Solver(plan, indexedID, new BnBStrategy()).init();
        solverBNB.solve();
    }

    public void loadRequests(String filePath){
        try{
            File xmlFile = new File(filePath);
            // Verifying if the file exists
            if (!xmlFile.exists()){
                throw new FileNotFoundException("Le fichier '" + filePath + "' est introuvable.");
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
                    throw new Exception("L'intersection '" + deliveryAdress + "' n'existe pas !");
                }
                DeliveryRequest deliveryRequest = new DeliveryRequest(intersection);
                deliveryRequestList.add(deliveryRequest);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }




}
