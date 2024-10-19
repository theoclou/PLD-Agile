package com.pld.agile.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Plan {
    private List<Section> sections = new ArrayList<>();
    private Set<Intersection> intersections = new HashSet<>();
    private Map<String, Integer> indexes = new HashMap<>();
    private ArrayList<ArrayList<Integer>> costsMatrix = new ArrayList<>();
    /*
     * public void readXml(String filePath) {
     * try {
     * File xmlFile = new File(filePath);
     * DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     * DocumentBuilder builder = factory.newDocumentBuilder();
     * Document document = builder.parse(xmlFile);
     * 
     * // Reading the intersections
     * NodeList intersectionElements = document.getElementsByTagName("noeud");
     * for (int i = 0; i < intersectionElements.getLength(); i++) {
     * Element element = (Element) intersectionElements.item(i);
     * String id = element.getAttribute("id");
     * double latitude = Double.parseDouble(element.getAttribute("latitude"));
     * double longitude = Double.parseDouble(element.getAttribute("longitude"));
     * 
     * // Create the Intersection objects
     * Intersection intersection = new Intersection();
     * intersection.init(id, latitude, longitude);
     * intersections.add(intersection);
     * }
     * 
     * // Reading the sections
     * NodeList sectionElements = document.getElementsByTagName("troncon");
     * for (int i = 0; i < sectionElements.getLength(); i++) {
     * Element element = (Element) sectionElements.item(i);
     * String originId = element.getAttribute("origine");
     * String destinationId = element.getAttribute("destination");
     * double length = Double.parseDouble(element.getAttribute("longueur"));
     * String name = element.getAttribute("nomRue");
     * 
     * // Create the Section objects
     * Section section = new Section();
     * section.init(originId, destinationId, name, length);
     * sections.add(section);
     * }
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     */

    // using Djikstra's algorithm to find the shortest path between two
    // intersections
    // make adjacency matrix
    public void reindexIntersections() {
        int i = 0;
        for (Intersection intersection : intersections) {
            String id = intersection.getId();
            indexes.put(id, i);
            i += 1;
        }
    }

    private void initializeMatrix() {

        for (int i = 0; i < intersections.size(); i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < intersections.size(); j++) {
                row.add(0); // Initialize with 0 or any default value
            }
            costsMatrix.add(row);
        }
    }

    public void makeCostsMatrix() {
        // Initialize the adjacency matrix with the size of the intersections
        initializeMatrix();
        for(Section section:sections)
        {
            
        }
    }

    public List<Section> findShortestPath(Intersection departure, Intersection arrival) {
        return null;
    }
}