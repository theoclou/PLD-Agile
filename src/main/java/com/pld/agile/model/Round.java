package com.pld.agile.model;

import java.util.List;
import java.util.Map;

public class Round {
    private List<Courier> courierList;
    private List<DeliveryRequest> deliveryRequestList;
    private Map<Courier, DeliveryTour> tourAttribution;

    public Round() {}

    public void init(List<Courier> courierList, List<DeliveryRequest> deliveryRequestList) {
        this.courierList = courierList;
        this.deliveryRequestList = deliveryRequestList;
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
    }

    public void loadRequests(String fileName){
        //TODO
        // Load the requests and create objects
    }




}
