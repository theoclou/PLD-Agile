package com.pld.agile.model.graph;

import java.io.FileNotFoundException;
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


import com.pld.agile.model.XMLReader;
import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Section;
import org.springframework.web.multipart.MultipartFile;

import javax.management.InstanceNotFoundException;

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

    public void PlanInit(String filePath) throws FileNotFoundException, IllegalArgumentException, InstanceNotFoundException {
        try {
            // Load and validate XML data
            Map<String, Object> data = XMLReader.LoadPlanByPath(filePath);

            // Validate and assign each required field with type checking
            try {
                // Sections
                if (!data.containsKey("sections")) {
                    throw new IllegalArgumentException("Missing required field: sections");
                }
                this.sections = (List<Section>) data.get("sections");
                if (this.sections == null) {
                    throw new IllegalArgumentException("Sections data is null");
                }

                // Intersections
                if (!data.containsKey("intersections")) {
                    throw new IllegalArgumentException("Missing required field: intersections");
                }
                this.intersections = (List<Intersection>) data.get("intersections");
                if (this.intersections == null) {
                    throw new IllegalArgumentException("Intersections data is null");
                }

                // IntersectionMap
                if (!data.containsKey("intersectionMap")) {
                    throw new IllegalArgumentException("Missing required field: intersectionMap");
                }
                this.intersectionMap = (Map<String, Intersection>) data.get("intersectionMap");
                if (this.intersectionMap == null) {
                    throw new IllegalArgumentException("IntersectionMap data is null");
                }

                // Indexes
                if (!data.containsKey("indexes")) {
                    throw new IllegalArgumentException("Missing required field: indexes");
                }
                this.indexes = (Map<String, Integer>) data.get("indexes");
                if (this.indexes == null) {
                    throw new IllegalArgumentException("Indexes data is null");
                }

                // ReverseIndexes
                if (!data.containsKey("reverseIndexes")) {
                    throw new IllegalArgumentException("Missing required field: reverseIndexes");
                }
                this.reverseIndexes = (Map<Integer, String>) data.get("reverseIndexes");
                if (this.reverseIndexes == null) {
                    throw new IllegalArgumentException("ReverseIndexes data is null");
                }

                // CostsMatrix
                if (!data.containsKey("costsMatrix")) {
                    throw new IllegalArgumentException("Missing required field: costsMatrix");
                }
                this.costsMatrix = (ArrayList<ArrayList<Double>>) data.get("costsMatrix");
                if (this.costsMatrix == null) {
                    throw new IllegalArgumentException("CostsMatrix data is null");
                }

            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid data type in XML file: " + e.getMessage(), e);
            }

        } catch (FileNotFoundException e) {
            throw e; // Rethrow file not found errors directly
        } catch (IllegalArgumentException e) {
            throw e; // Rethrow validation errors
        } catch (InstanceNotFoundException e) {
            throw e; //Rethrow instance not found errors
        }catch (Exception e) {
            // Wrap unexpected exceptions
            throw new IllegalArgumentException("Unexpected error initializing plan: " + e.getMessage(), e);
        }
    }


    public void PlanInit(MultipartFile file)
    {
        Map<String, Object> data = XMLReader.LoadPlanByFile(file);
        this.sections = (List<Section>) data.get("sections");
        this.intersections = (List<Intersection>) data.get("intersections");
        this.intersectionMap = (Map<String, Intersection>) data.get("intersectionMap");
        this.indexes = (Map<String, Integer>) data.get("indexes");
        this.reverseIndexes = (Map<Integer, String>) data.get("reverseIndexes");
        this.costsMatrix = (ArrayList<ArrayList<Double>>) data.get("costsMatrix");

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
    public Intersection getIntersectionById(String id) {
        return intersectionMap.get(id);
    }

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
        if (path.size() == 1 && path.get(0) != origin) {
            System.out.println("No path found between origin and destination.");
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
            tour.addAll(findShortestPath(i, i + 1));
        }
        Set<Integer> uniqueTour = new HashSet<>(tour);

        // Convert back to a list 
        tour = new ArrayList<>(uniqueTour);
        tour.add(tour.get(0));
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


    
}