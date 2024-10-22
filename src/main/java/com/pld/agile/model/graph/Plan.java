package com.pld.agile.model.graph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.management.InstanceNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Section;

public class Plan {
    @SuppressWarnings("FieldMayBeFinal")
    private List<Section> sections = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private List<Intersection> intersections = new ArrayList<>();
    private Map<String, Intersection> intersectionMap = new HashMap<>();
    @SuppressWarnings("FieldMayBeFinal")
    private Map<String, Integer> indexes = new HashMap<>();
    private Map<Integer, String> reverseIndexes = new HashMap<>();

    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<ArrayList<Double>> costsMatrix = new ArrayList<>();
    private ArrayList<Integer> tour=new ArrayList<>();
    private ArrayList<String> IntersectionsTour=new ArrayList<>();

    public Plan() {
    }

    @SuppressWarnings("UseSpecificCatch")
    public void readXml(String filePath) throws Exception{
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
                try {
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    // Create the Intersection objects
                    Intersection intersection = new Intersection();
                    intersection.initialisation(id, latitude, longitude);
                    intersectionMap.put(id, intersection);
                    intersections.add(intersection);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Invalid numeric value in an intersection : " + e.getMessage());
                }
            }

            // Reading the sections
            NodeList sectionElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < sectionElements.getLength(); i++) {
                Element element = (Element) sectionElements.item(i);
                try {
                    String originId = element.getAttribute("origine");
                    String destinationId = element.getAttribute("destination");
                    double length = Double.parseDouble(element.getAttribute("longueur"));
                    String name = element.getAttribute("nomRue");

                    // Create the Section objects
                    Section section = new Section();
                    section.initialisation(originId, destinationId, name, length);
                    sections.add(section);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Invalid numeric value in a section: " + e.getMessage());
                }
            }

            for (int i=0; i<sections.size(); i++) {
                Boolean originFind = false;
                Boolean destinationFind = false;
                for (int j=0; j<intersections.size(); j++) {
                    if (intersections.get(j).getId().equals(sections.get(i).getOrigin())) {
                        originFind = true;
                    }
                    if (intersections.get(j).getId().equals(sections.get(i).getDestination())) {
                        destinationFind = true;
                    }
                }
                if (!originFind || !destinationFind) {
                    throw new InstanceNotFoundException("The XML file is missing required origin or destination intersections.");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e; // Propagate exception if file not found
        } catch (SAXException e) {
            // Captures errors related to malformed XML parsing
            throw new Exception("Malformed XML file : " + e.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw e;
        }
        catch (InstanceNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("Nombre d'intersections : " + intersections.size());
        System.out.println("Nombre de tronçons : " + sections.size());
    }

    // Méthode pour afficher le contenu du fichier XML
    private void displayXmlContent(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n"); // Ajoute chaque ligne avec un saut de ligne
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Contenu du fichier XML :");
        System.out.println(content.toString()); // Affiche le contenu
    }

    public void resetMap() {
        intersectionMap.clear();
        intersections.clear();
        sections.clear();
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public Intersection getIntersectionById(String id) {
        return intersectionMap.get(id);
    }

    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
        intersectionMap.put(intersection.getId(), intersection);
    }
    public Integer getIndexById(String id) {
        return indexes.get(id);
    }
    public String getIdByIndex(Integer index) { return reverseIndexes.get(index); }

    // Reindex intersections based on their IDs
    public void reIndexIntersections() {
        int i = 0;
        for (Intersection intersection : intersections) {
            String id = intersection.getId();
            indexes.put(id, i);
            reverseIndexes.put(i, id);
            i += 1;
        }
    }
    private void reverseIndexation()
    {
        for (Map.Entry<String, Integer> pair : indexes.entrySet()) {
            reverseIndexes.put(pair.getValue(),pair.getKey());
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

    private Map<String, Object> dijkstraAlgorithm(int origin, int destination) {
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

        // Return the total distance and the previous nodes
        Map<String, Object> result = new HashMap<>();
        result.put("distance", distances[destination]);
        result.put("previousNodes", previousNodes);
        return result;
    }

    // Function to return the shortest path
    public List<Integer> findShortestPath(int origin, int destination) {
        Map<String, Object> result = dijkstraAlgorithm(origin, destination);
        int[] previousNodes = (int[]) result.get("previousNodes");
        // Reconstruct and return the shortest path
        return reconstructPath(destination, origin, previousNodes);
    }

    // Function to return the total distance
    public double findShortestDistance(int origin, int destination) {
        Map<String, Object> result = dijkstraAlgorithm(origin, destination);
        double totalDistance = (double) result.get("distance");
        // If unreachable, return -1
        return totalDistance == Double.MAX_VALUE ? -1 : totalDistance;
    }
    public void constructTour(List<Integer> path)
    {
        for (int i=0;i<path.size()-1;i++)
        {
            tour.addAll(findShortestPath(i, i+1));
        }
    }
    public List<String> makeIntersectionsTour()
    {
        for (Integer point : tour) {
            String intersectionId=reverseIndexes.get(point);
            IntersectionsTour.add(intersectionId);
        }
        return IntersectionsTour;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Plan Details:\n");

        // Print Cost Matrix
        sb.append("Cost Matrix:\n");
        for (int i = 0; i < costsMatrix.size(); i++) {
            for (int j = 0; j < costsMatrix.get(i).size(); j++) {
                double cost = costsMatrix.get(i).get(j);
                if (cost == Double.MAX_VALUE) {
                    sb.append(" ∞ ");
                } else {
                    sb.append(String.format("%6.2f", cost)).append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }


    //--------------------------------------------------------------------------------------

    public void readXmlbyFile(MultipartFile file) throws Exception {
        File tempFile = null;
        try {
            // Convert MultipartFile to a temporary file
            tempFile = File.createTempFile("uploaded-", ".xml");
            file.transferTo(tempFile);

            // Parse the XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(tempFile);

            // Read intersections
            NodeList intersectionElements = document.getElementsByTagName("noeud");
            for (int i = 0; i < intersectionElements.getLength(); i++) {
                Element element = (Element) intersectionElements.item(i);
                String id = element.getAttribute("id");
                double latitude = Double.parseDouble(element.getAttribute("latitude"));
                double longitude = Double.parseDouble(element.getAttribute("longitude"));

                Intersection intersection = new Intersection();
                intersection.initialisation(id, latitude, longitude);
                intersectionMap.put(id, intersection);
                intersections.add(intersection);
            }

            // Read sections
            NodeList sectionElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < sectionElements.getLength(); i++) {
                Element element = (Element) sectionElements.item(i);
                String originId = element.getAttribute("origine");
                String destinationId = element.getAttribute("destination");
                double length = Double.parseDouble(element.getAttribute("longueur"));
                String name = element.getAttribute("nomRue");

                Section section = new Section();
                section.initialisation(originId, destinationId, name, length);
                sections.add(section);
            }

            // Validate that all sections have valid intersections
            for (Section section : sections) {
                boolean originFound = intersections.stream().anyMatch(i -> i.getId().equals(section.getOrigin()));
                boolean destinationFound = intersections.stream().anyMatch(i -> i.getId().equals(section.getDestination()));
                if (!originFound || !destinationFound) {
                    throw new InstanceNotFoundException("The XML file is missing required origin or destination intersections.");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            resetMap(); // Clear data on file not found
            throw e;
        } catch (SAXException e) {
            resetMap(); // Clear data on XML parsing error
            throw new Exception("Malformed XML file: " + e.getMessage());
        } catch (NumberFormatException e) {
            resetMap(); // Clear data on numeric parsing error
            throw new NumberFormatException("Invalid numeric value: " + e.getMessage());
        } finally {
            // Cleanup temporary file in case of exception
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        System.out.println("Number of intersections: " + intersections.size());
        System.out.println("Number of sections: " + sections.size());
    }


}