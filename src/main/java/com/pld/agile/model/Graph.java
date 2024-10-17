package com.pld.agile.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.text.Segment;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Graph {
    private Map<Intersection, List<Section>> adjencyList = new HashMap<>();
    private Map<String, Intersection> intersectionMap = new HashMap<>();  // Pour rechercher les intersections par ID

    public void readXml(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Lecture des intersections
            NodeList intersectionElements = document.getElementsByTagName("noeud");
            for (int i = 0; i < intersectionElements.getLength(); i++) {
                Element element = (Element) intersectionElements.item(i);
                String id = element.getAttribute("id");
                double latitude = Double.parseDouble(element.getAttribute("latitude"));
                double longitude = Double.parseDouble(element.getAttribute("longitude"));
                
                // Création de l'objet Intersection
                Intersection intersection = new Intersection(id, latitude, longitude);
                adjencyList.put(intersection, new ArrayList<>());  // Initialiser une liste vide pour chaque intersection
                intersectionMap.put(id, intersection);  // Sauvegarde dans la Map pour référence facile
            }

            // Lecture des tronçons
            NodeList sectionElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < sectionElements.getLength(); i++) {
                Element element = (Element) sectionElements.item(i);
                String originId = element.getAttribute("origine");
                String destinationId = element.getAttribute("destination");
                double length = Double.parseDouble(element.getAttribute("longueur"));
                String name = element.getAttribute("nomRue");
                
                // Recherche des intersections d'origine et de destination
                Intersection origin = intersectionMap.get(originId);
                Intersection destination = intersectionMap.get(destinationId);

                // Vérification que les intersections existent
                if (origin != null && destination != null) {
                    // Création du tronçon et ajout à la liste d'adjacence
                    Section section = new Section(origin, destination, name, length);
                    adjencyList.get(origin).add(section);
                } else {
                    System.err.println("Erreur : Intersection manquante pour origine " + originId + " ou destination " + destinationId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour afficher le graphe
    public void displayGraph() {
        for (Map.Entry<Intersection, List<Section>> entry : adjencyList.entrySet()) {
            Intersection intersection = entry.getKey();
            List<Section> sections = entry.getValue();
            System.out.println("Intersection " + intersection + " a les tronçons suivants : ");
            for (Section section : sections) {
                System.out.println("    " + section);
            }
        }
    }
}
