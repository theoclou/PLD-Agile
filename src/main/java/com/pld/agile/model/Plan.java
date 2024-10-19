package com.pld.agile.model;

import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Plan {
    private List<Section> sections = new ArrayList<>();
    private List<Intersection> intersections = new ArrayList<>();
    private Map<String, Integer> indexes = new HashMap<>();
    private ArrayList<ArrayList<Double>> costsMatrix = new ArrayList<>();

    public void readXml(String filePath) {
        try {
            File xmlFile = new File(filePath);

            // Check if the file exists
            if (!xmlFile.exists()) {
                throw new FileNotFoundException("The file '" + filePath + "' is not found.");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Reading the intersections
            NodeList intersectionElements = document.getElementsByTagName("noeud");
            for (int i = 0; i < intersectionElements.getLength(); i++) {
                Element element = (Element) intersectionElements.item(i);
                String id = element.getAttribute("id");
                double latitude = Double.parseDouble(element.getAttribute("latitude"));
                double longitude = Double.parseDouble(element.getAttribute("longitude"));

                // Create the Intersection objects
                Intersection intersection = new Intersection();
                intersection.initialisation(id, latitude, longitude);
                intersections.add(intersection);
            }

            // Reading the sections
            NodeList sectionElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < sectionElements.getLength(); i++) {
                Element element = (Element) sectionElements.item(i);
                String originId = element.getAttribute("origine");
                String destinationId = element.getAttribute("destination");
                double length = Double.parseDouble(element.getAttribute("longueur"));
                String name = element.getAttribute("nomRue");

                // Create the Section objects
                Section section = new Section();
                section.initialisation(originId, destinationId, name, length);
                sections.add(section);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    // Reindex intersections based on their IDs
    public void reIndexIntersections() {
        int i = 0;
        for (Intersection intersection : intersections) {
            String id = intersection.getId();
            indexes.put(id, i);
            i += 1;
        }
    }

    // Initialize the cost matrix 0 or infinity values
    private void initializeCostsMatrix() {
        int size = intersections.size();
        for (int i = 0; i < size; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    row.add(0.0); // Distance to self is 0
                } else {
                    row.add(Double.MAX_VALUE); // Initialize with a large value to indicate no direct connection
                }
            }
            costsMatrix.add(row);
        }
    }
    // function that fills the cost matrix according to the sections read from XML
    // file

    private void fillCostsMAtrix() {
        // Set the costs based on the sections
        for (Section section : sections) {
            String originId = section.getOrigin();
            String destinationId = section.getDestination();
            double length = section.getLength();

            int originIndex = indexes.get(originId);
            int destinationIndex = indexes.get(destinationId);

            // Set the distance (or cost) for the matrix
            costsMatrix.get(originIndex).set(destinationIndex, length);
        }
    }

    public void makeCostsMatrix() {
        // Initialize the adjacency matrix with the size of the intersections
        initializeCostsMatrix();
        fillCostsMAtrix();
    }

    private double[] initializeDistances(int numNodes, int origin) {
        double[] distances = new double[numNodes];
        Arrays.fill(distances, Double.MAX_VALUE);
        distances[origin] = 0.0;
        return distances;
    }

    // Function to initialize the previous nodes array
    private int[] initializePreviousNodes(int numNodes) {
        int[] previousNodes = new int[numNodes];
        Arrays.fill(previousNodes, -1);
        return previousNodes;
    }

    private void updateNeighborDistances(int currentNode, int numNodes, double[] distances, boolean[] visited,
            int[] previousNodes, PriorityQueue<Integer> priorityQueue, ArrayList<ArrayList<Double>> costsMatrix) {
        for (int neighbor = 0; neighbor < numNodes; neighbor++) {
            if (!visited[neighbor] && costsMatrix.get(currentNode).get(neighbor) < Double.MAX_VALUE) {
                double newDist = distances[currentNode] + costsMatrix.get(currentNode).get(neighbor);

                if (newDist < distances[neighbor]) {
                    distances[neighbor] = newDist;
                    previousNodes[neighbor] = currentNode;
                    priorityQueue.add(neighbor);
                }
            }
        }
    }

    private List<Integer> reconstructPath(int destination, int origin, int[] previousNodes) {
        List<Integer> path = new ArrayList<>();
        for (int node = destination; node != -1; node = previousNodes[node]) {
            path.add(node);
        }
        Collections.reverse(path);

        // Check if the destination is reachable
        if (path.size() == 1 && path.get(0) != origin) {
            System.out.println("No path found between origin and destination.");
            return new ArrayList<>();
        }

        return path;
    }

    public List<Integer> findShortestPath(int origin, int destination) {
        int numNodes = costsMatrix.size();

        // Initialize required arrays and structures
        double[] distances = initializeDistances(numNodes, origin);
        boolean[] visited = new boolean[numNodes];
        int[] previousNodes = initializePreviousNodes(numNodes);

        // Priority queue to select the node with the minimum distance
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(node -> distances[node]));
        priorityQueue.add(origin);

        // Process nodes using Dijkstra's algorithm
        while (!priorityQueue.isEmpty()) {
            int currentNode = priorityQueue.poll();
            // If we reached the destination, stop processing
            if (currentNode == destination) {
                break;
            }
            // Mark the node as visited
            visited[currentNode] = true;

            // Explore neighbors of the current node
            updateNeighborDistances(currentNode, numNodes, distances, visited, previousNodes, priorityQueue,
                    costsMatrix);
        }
        // Reconstruct and return the shortest path
        return reconstructPath(destination, origin, previousNodes);
    }
}












    /*
     * public List<Integer> findShortestPath(int origin, int destination) {
     * int numNodes = costsMatrix.size();
     * 
     * // Distance array to store the shortest distance from the origin to each node
     * double[] distances = new double[numNodes];
     * // Array to track if the node has been processed
     * boolean[] visited = new boolean[numNodes];
     * // Array to store the previous node in the shortest path
     * int[] previousNodes = new int[numNodes];
     * 
     * // Initialize distances as infinity, visited as false, and previous nodes as
     * -1
     * Arrays.fill(distances, Double.MAX_VALUE);
     * Arrays.fill(visited, false);
     * Arrays.fill(previousNodes, -1);
     * 
     * // Distance from origin to itself is 0
     * distances[origin] = 0.0;
     * 
     * // Priority queue to get the node with the minimum distance
     * PriorityQueue<Integer> priorityQueue = new
     * PriorityQueue<>(Comparator.comparingDouble(node -> distances[node]));
     * priorityQueue.add(origin);
     * 
     * while (!priorityQueue.isEmpty()) {
     * // Get the node with the minimum distance
     * int currentNode = priorityQueue.poll();
     * 
     * // If we reached the destination, stop
     * if (currentNode == destination) {
     * break;
     * }
     * 
     * // Mark the node as visited
     * visited[currentNode] = true;
     * 
     * // Explore the neighbors of the current node
     * for (int neighbor = 0; neighbor < numNodes; neighbor++) {
     * // Only consider unvisited nodes with a connection (non-infinite distance)
     * if (!visited[neighbor] && costsMatrix.get(currentNode).get(neighbor) <
     * Double.MAX_VALUE) {
     * // Calculate the new distance
     * double newDist = distances[currentNode] +
     * costsMatrix.get(currentNode).get(neighbor);
     * 
     * // If a shorter path is found, update the distance and previous node
     * if (newDist < distances[neighbor]) {
     * distances[neighbor] = newDist;
     * previousNodes[neighbor] = currentNode;
     * priorityQueue.add(neighbor);
     * }
     * }
     * }
     * }
     * 
     * // Reconstruct the shortest path
     * List<Integer> path = new ArrayList<>();
     * for (int node = destination; node != -1; node = previousNodes[node]) {
     * path.add(node);
     * }
     * Collections.reverse(path);
     * 
     * // If the destination is not reachable, the path will only contain the
     * // destination node
     * if (path.size() == 1 && path.get(0) != origin) {
     * System.out.println("No path found between origin and destination.");
     * return new ArrayList<>();
     * }
     * 
     * return path;
     * }
     */
    // Function to initialize the distances array