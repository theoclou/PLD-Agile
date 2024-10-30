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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

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
/**
 * The {@code Plan} class is responsible for reading and processing a city plan
 * in XML format, which consists of intersections and sections between them.
 * It also calculates the cost matrix for the graph and computes the shortest
 * paths between intersections using Dijkstra's algorithm.
 */
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
    private ArrayList<Integer> tour = new ArrayList<>();
    private ArrayList<String> IntersectionsTour = new ArrayList<>();
     /**
     * Default constructor for the {@code Plan} class.
     */
    public Plan() {
    }

    @SuppressWarnings("UseSpecificCatch")
    /**
     * Reads an XML file containing the plan of the city and populates the list of
     * intersections and sections. It parses the XML, creates intersection and
     * section objects, and stores them in their respective lists.
     *
     * @param filePath the path to the XML file
     * @throws Exception if an error occurs during the reading or parsing process
     */
    public void readXml(String filePath) throws Exception {
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

            for (int i = 0; i < sections.size(); i++) {
                Boolean originFind = false;
                Boolean destinationFind = false;
                for (int j = 0; j < intersections.size(); j++) {
                    if (intersections.get(j).getId().equals(sections.get(i).getOrigin())) {
                        originFind = true;
                    }
                    if (intersections.get(j).getId().equals(sections.get(i).getDestination())) {
                        destinationFind = true;
                    }
                }
                if (!originFind || !destinationFind) {
                    throw new InstanceNotFoundException(
                            "The XML file is missing required origin or destination intersections.");
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
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("Nombre d'intersections : " + intersections.size());
        System.out.println("Nombre de tronçons : " + sections.size());
    }
    /**
     * Resets the map, clearing all intersections and sections previously loaded.
     */
    public void resetMap() {
        intersectionMap.clear();
        intersections.clear();
        sections.clear();
    }
    /**
     * Returns the list of sections in the plan.
     *
     * @return the list of {@code Section} objects
     */
    public List<Section> getSections() {
        return sections;
    }

    /**
     * Returns the list of intersections in the plan.
     *
     * @return the list of {@code Intersection} objects
     */
    public List<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Returns the intersection by its ID.
     *
     * @param id the ID of the intersection
     * @return the {@code Intersection} object with the given ID, or {@code null} if not found
     */
    public Intersection getIntersectionById(String id) {return intersectionMap.get(id);}

    /**
     * Adds a new intersection to the plan.
     *
     * @param intersection the {@code Intersection} object to add
     */
    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
        intersectionMap.put(intersection.getId(), intersection);
    }

    /**
     * Returns the index of an intersection given its ID.
     *
     * @param id the ID of the intersection
     * @return the index of the intersection in the list
     */
    public Integer getIndexById(String id) {
        return indexes.get(id);
    }

    /**
     * Returns the ID of an intersection given its index.
     *
     * @param index the index of the intersection
     * @return the ID of the intersection at the given index
     */
    public String getIdByIndex(Integer index) {
        return reverseIndexes.get(index);
    }

    /**
     * Re-indexes all intersections in the plan based on their IDs.
     */
    private void reIndexIntersections() {
        int i = 0;
        for (Intersection intersection : intersections) {
            String id = intersection.getId();
            indexes.put(id, i);
            reverseIndexes.put(i, id);
            i += 1;
        }
    }

    /**
     * gets all intersection's ids using the predefined indexes in <code> reIndexIntersections() </code>.
     */
    private void reverseIndexation() {
        for (Map.Entry<String, Integer> pair : indexes.entrySet()) {
            reverseIndexes.put(pair.getValue(), pair.getKey());
        }
    }

    /**
     * Initializes the cost matrix for the graph with 0 or infinity values. This
     * method sets the distance from a node to itself as 0 and sets all other
     * distances to infinity.
     */
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
    /**
     * Fills the cost matrix based on the sections read from the XML file.
     */
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
    /**
     * makes the cost matrix using the values extarcted from the file
     */
    private void makeCostsMatrix() {
        // Initialize the adjacency matrix with the size of the intersections
        initializeCostsMatrix();
        fillCostsMAtrix();
    }
    /**
     * Processes the data by creating indexing the ids of the intersections of the map  and creating the cost matrix.
     */
    public void preprocessData() {
        reIndexIntersections();
        reverseIndexation();
        makeCostsMatrix();
    }

    /**
     * Initializes an array of distances for Dijkstra's algorithm. All distances are
     * initially set to {@code Double.MAX_VALUE} (infinity), except for the origin node,
     * which is set to 0.
     *
     * @param numNodes the total number of nodes in the graph
     * @param origin   the index of the origin node
     * @return a {@code double[]} array where the distance to the origin node is 0 and all other distances are infinity
     */
    private double[] initializeDistances(int numNodes, int origin) {
        double[] distances = new double[numNodes];
        Arrays.fill(distances, Double.MAX_VALUE);
        distances[origin] = 0.0;
        return distances;
    }

    /**
     * Initializes the previous nodes array for Dijkstra's algorithm. Each node is
     * initially set to -1, meaning no predecessor has been assigned yet.
     *
     * @param numNodes the total number of nodes in the graph
     * @return an {@code int[]} array where all values are set to -1
     */
    private int[] initializePreviousNodes(int numNodes) {
        int[] previousNodes = new int[numNodes];
        Arrays.fill(previousNodes, -1);
        return previousNodes;
    }

    /**
     * Updates the distances of neighboring nodes for the current node during Dijkstra's algorithm.
     * This method checks unvisited neighbors and updates their distance and predecessor
     * if a shorter path is found through the current node. It then adds the neighbor to
     * the priority queue for further exploration.
     *
     * @param currentNode   the index of the current node being processed
     * @param numNodes      the total number of nodes in the graph
     * @param distances     the array of current shortest distances from the origin
     * @param visited       the array indicating whether a node has been visited
     * @param previousNodes the array storing the previous node for each node in the shortest path
     * @param priorityQueue the priority queue for selecting the next node to process
     * @param costsMatrix   the adjacency matrix representing the cost between nodes
     */
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


    /**
     * Reconstructs the shortest path from the origin to the destination using the previous
     * nodes array generated by Dijkstra's algorithm. The path is traced backwards from
     * the destination to the origin.
     *
     * @param destination   the index of the destination node
     * @param origin        the index of the origin node
     * @param previousNodes the array storing the previous node for each node in the shortest path
     * @return a {@code List<Integer>} representing the shortest path from the origin to the destination.
     *         If no path exists, an empty list is returned.
     */
    private List<Integer> reconstructPath(int destination, int origin, int[] previousNodes) {
        List<Integer> path = new ArrayList<>();
        for (int node = destination; node != -1; node = previousNodes[node]) {
            path.add(node);
        }
        Collections.reverse(path);

        // Check if the destination is reachable
        if (path.size() == 1 && path.getFirst() != origin) {

            System.out.println("No path found between"+ origin+" and "+destination+".");
            return new ArrayList<>();
        }

        return path;
    }

    /**
     * Uses Dijkstra's algorithm to compute the shortest path between two nodes
     * (origin and destination) in the graph, based on the cost matrix.
     *
     * @param origin      the index of the starting node
     * @param destination the index of the destination node
     * @return a map containing the total distance and the array of previous nodes
     */
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

    /**
     * Finds the shortest path between two nodes (origin and destination) and returns
     * it as a list of node indices.
     *
     * @param origin      the index of the starting node
     * @param destination the index of the destination node
     * @return a list of node indices representing the shortest path
     */
    public List<Integer> findShortestPath(int origin, int destination) {
        Map<String, Object> result = dijkstraAlgorithm(origin, destination);
        int[] previousNodes = (int[]) result.get("previousNodes");
        // Reconstruct and return the shortest path
        return reconstructPath(destination, origin, previousNodes);
    }

    /**
     * Finds the shortest distance between two nodes (origin and destination) and returns a double
     *
     * @param origin      the index of the starting node
     * @param destination the index of the destination node
     * @return the cost of the shortest path
     */
    public double findShortestDistance(int origin, int destination) {
        Map<String, Object> result = dijkstraAlgorithm(origin, destination);
        double totalDistance = (double) result.get("distance");
        // If unreachable, return -1
        return totalDistance == Double.MAX_VALUE ? -1 : totalDistance;
    }

    /**
     * Constructs a tour by finding the shortest paths between consecutive nodes
     * and ensuring that no duplicates exist in the tour.
     *
     * @param path the list of nodes to visit
     */
    private void constructTour(List<Integer> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            if (i==0)
            {
                tour.addAll(findShortestPath(path.get(i), path.get(i + 1)));
            }
            else
            {
                List<Integer> subPath = findShortestPath(path.get(i), path.get(i + 1));
                tour.addAll(subPath.subList(1, subPath.size()));
            }

        }

        tour.add(tour.getFirst());
    }

    /**
     * Converts the tour of node indices to a tour of intersection IDs.
     *
     * @return the list of intersection IDs in the order of the tour
     */
    private List<String> makeIntersectionsTour() {
        for (Integer point : tour) {
            String intersectionId = reverseIndexes.get(point);
            IntersectionsTour.add(intersectionId);
        }
        return IntersectionsTour;
    }
    /**
     * Computes the complete tour of intersections based on a given path and returns
     * the result as a list of intersection IDs.
     *
     * @param path the list of nodes to visit
     * @return the list of intersection IDs in the order of the tour
     */
    public List<String> computeTour(List<Integer> path) {
        constructTour(path);
        List<String> finalResult = makeIntersectionsTour();
        System.out.println(finalResult);
        return finalResult;
    }
    /**
     * Formats the input list of intersection IDs into a list of node indices.
     *
     * @param idIntersections the list of intersection IDs
     * @return the formatted list of node indices
     */
    public List<Integer> formatInput(List<String> idIntersections) {
        List<Integer> formattedInput = new ArrayList<>();
        for (String id : idIntersections) {
            formattedInput.add(indexes.get(id));
        }
        return formattedInput;
    }


    // --------------------------------------------------------------------------------------
    /**
     * Reads an XML file from a {@code MultipartFile} and parses its content into the
     * list of intersections and sections.
     *
     * @param file the uploaded XML file
     * @throws Exception if an error occurs during the reading or parsing process
     */
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
                boolean destinationFound = intersections.stream()
                        .anyMatch(i -> i.getId().equals(section.getDestination()));
                if (!originFound || !destinationFound) {
                    throw new InstanceNotFoundException(
                            "The XML file is missing required origin or destination intersections.");
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

    public Map<String, Intersection> getIntersectionMap() {
        return intersectionMap;
    }
public Map<String,Integer> getIndexesmap()
{
    return this.indexes;
}
}
