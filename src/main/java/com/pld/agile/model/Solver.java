package com.pld.agile.model;

import java.time.LocalTime;
import java.util.*;

import com.pld.agile.model.graph.CompleteGraph;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.BnBStrategy;
import com.pld.agile.model.strategy.SolvingStrategy;

/**
 * The {@code Solver} class is responsible for solving a Traveling Salesman
 * Problem (TSP)
 * based on a plan of intersections and sections. It utilizes a strategy pattern
 * for
 * solving the problem and works with a complete graph representation.
 */
public class Solver {
    private List<Integer> vertices = new ArrayList<>();
    private ArrayList<ArrayList<Double>> completeMatrix = new ArrayList<>();
    private Plan plan;
    private SolvingStrategy solvingStrategy;
    private CompleteGraph g;
    private Map<String, Object> resultPoint;
    private List <Integer> bestPath=new ArrayList<>();
    private Map<Integer, Integer> originalToCurrentIndexMap = new HashMap<>();
    private Map<Integer, Integer> currentToOriginalIndexMap = new HashMap<>();
    /**
     * Constructs a {@code Solver} with the given plan, vertices, and solving
     * strategy.
     *
     * @param plan            the {@code Plan} object representing intersections and
     *                        sections
     * @param vertices        the list of vertices to include in the TSP
     * @param solvingStrategy the strategy used to solve the TSP
     */
    public Solver(Plan plan, List<Integer> vertices, SolvingStrategy solvingStrategy) {
        this.plan = plan;
        this.vertices = vertices;
        this.solvingStrategy = solvingStrategy;
        this.resultPoint = new HashMap<>();
    }

    /**
     * Initializes the solver by creating a complete graph using the given plan and
     * vertices.
     *
     * @return the {@code Solver} object after initialization
     */
    public Solver init() {
        this.createCompleteGraph();
        return this;
    }

    /**
     * Fills the {@code completeMatrix} with distances between vertices to create
     * a complete graph representation.
     */
    public CompleteGraph createCompleteGraph() {
        completeMatrix.clear();
        int size = vertices.size();
        for (int i = 0; i < size; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    row.add(-1.0);
                } else {
                    Double distance = plan.findShortestDistance(vertices.get(i), vertices.get(j));
                    row.add(distance);
                }
            }
            completeMatrix.add(row);
        }
        g = new CompleteGraph(completeMatrix.size(), completeMatrix);
        return g;
    }

    /**
     * Solves the TSP using the provided solving strategy.
     */
    public void solve() {
        solvingStrategy.solve(g);
    }

    /**
     * Returns the list of vertices involved in the TSP.
     *
     * @return the list of vertices
     */
    public List<Integer> getVertices() {
        return this.vertices;
    }

    /**
     * Sets the list of vertices for the TSP.
     *
     * @param vertices the list of vertices to set
     */
    public void setVertices(List<Integer> vertices) {
        this.vertices = vertices;
    }

    /**
     * Returns the complete distance matrix.
     *
     * @return the complete matrix representing distances between vertices
     */
    public ArrayList<ArrayList<Double>> getCompleteMatrix() {
        return this.completeMatrix;
    }

    /**
     * Sets the complete matrix representing distances between vertices.
     *
     * @param completeMatrix the complete matrix to set
     */
    public void setCompleteMatrix(ArrayList<ArrayList<Double>> completeMatrix) {
        this.completeMatrix = completeMatrix;
    }

    /**
     * Returns the plan used in this solver.
     *
     * @return the plan
     */
    public Plan getPlan() {
        return this.plan;
    }

    /**
     * Sets the plan for this solver.
     *
     * @param plan the plan to set
     */
    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    /**
     * Returns the best path found by the solving strategy.
     *
     * @return the best path as a list of vertices
     */
    public List<Integer> getBestPath() {
        if (bestPath == null || bestPath.isEmpty()) {
            bestPath = solvingStrategy.getBestPath();
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < bestPath.size(); i++) {
                result.add(vertices.get(bestPath.get(i)));
            }
            bestPath = result;
        }
        return new ArrayList<>(bestPath); // Return a defensive copy
    }

    /**
     * Returns the completeGraph object used to solve the tsp
     *
     * @return the complete graph
     */
    public CompleteGraph getCompleteGraph() {
        return this.g;
    }

    /**
     * Returns the cost of the best path found by the solving strategy.
     *
     * @return the best cost
     */
    public double getBestCost() {
        return solvingStrategy.getBestCost();
    }

    private void validatePath() {
        System.out.println("Validating path: " + bestPath);

        // Verify path exists and is not empty
        if (bestPath == null || bestPath.isEmpty()) {
            throw new IllegalStateException("Invalid path state: path is empty");
        }

        // Check for duplicates (excluding the warehouse which appears at start and end)
        Set<Integer> uniqueVertices = new HashSet<>(bestPath.subList(0, bestPath.size() - 1));
        if (uniqueVertices.size() != bestPath.size() - 1) {
            throw new IllegalStateException("Invalid path state: contains duplicates");
        }

        // Verify all vertices exist in the vertex list
        for (Integer vertex : bestPath) {
            if (!vertices.contains(vertex)) {
                throw new IllegalStateException(
                        String.format("Invalid path state: vertex %d not found in vertices %s",
                                vertex, vertices)
                );
            }
        }

        // Verify warehouse is at start and end
        if (!bestPath.get(0).equals(bestPath.get(bestPath.size() - 1))) {
            throw new IllegalStateException(
                    String.format("Invalid path state: different start (%d) and end (%d) points",
                            bestPath.get(0), bestPath.get(bestPath.size() - 1))
            );
        }

        System.out.println("Path validation successful");
    }

    public List<Integer> addDeliveryPoint(Integer intersection) {
        System.out.println("Current state before addition:");
        System.out.println("Vertices: " + vertices);
        System.out.println("Best path: " + bestPath);
        System.out.println("Attempting to add intersection: " + intersection);

        // Store the current state
        List<Integer> oldVertices = new ArrayList<>(vertices);
        List<Integer> oldPath = new ArrayList<>(bestPath);

        // Add the new vertex
        vertices.add(intersection);

        // Create new complete graph with added vertex
        g = createCompleteGraph();
        System.out.println("New graph size: " + g.getNbVertices());

        // Find the best insertion point
        double minimumDetour = Double.MAX_VALUE;
        int bestInsertionIndex = 0;

        // Check all possible insertion points in the current path
        for (int i = 0; i < bestPath.size() - 1; i++) {
            int currentVertex = vertices.indexOf(bestPath.get(i));
            int nextVertex = vertices.indexOf(bestPath.get(i + 1));
            int newVertexIndex = vertices.size() - 1; // Index of newly added vertex

            // Calculate detour cost
            double detourCost = g.getCost(currentVertex, newVertexIndex) +
                    g.getCost(newVertexIndex, nextVertex) -
                    g.getCost(currentVertex, nextVertex);

            if (detourCost < minimumDetour) {
                minimumDetour = detourCost;
                bestInsertionIndex = i + 1;
            }
        }

        // Insert the new vertex at the best position
        bestPath.add(bestInsertionIndex, intersection);

        // Update index mappings
        updateIndexMappings(oldVertices, vertices);

        System.out.println("State after addition:");
        System.out.println("Updated vertices: " + vertices);
        System.out.println("Updated best path: " + bestPath);
        System.out.println("Insertion position: " + bestInsertionIndex);
        System.out.println("Detour cost: " + minimumDetour);

        return new ArrayList<>(bestPath);
    }


    public List<Integer> deleteDeliveryPoint(Integer intersection) {
        System.out.println("Current state before deletion:");
        System.out.println("Vertices: " + vertices);
        System.out.println("Best path: " + bestPath);
        System.out.println("Attempting to delete intersection: " + intersection);

        // Find the intersection in our current vertices
        int vertexIndex = vertices.indexOf(intersection);
        if (vertexIndex == -1) {
            throw new IllegalArgumentException("Error: Intersection " + intersection + " not found in vertices");
        }

        // Store the current state of vertices and path
        List<Integer> oldVertices = new ArrayList<>(vertices);
        List<Integer> oldPath = new ArrayList<>(bestPath);

        // Remove from vertices
        vertices.remove(vertexIndex);

        // Update the complete graph with new vertex set
        g = createCompleteGraph();

        // Find and remove the intersection from the path
        int pathIndex = bestPath.indexOf(intersection);
        if (pathIndex == -1) {
            throw new IllegalArgumentException("Error: Intersection " + intersection + " not found in path");
        }
        bestPath.remove(pathIndex);

        // Update index mappings
        updateIndexMappings(oldVertices, vertices);

        // Adjust the remaining path indices based on the new vertex positions
        for (int i = 0; i < bestPath.size(); i++) {
            Integer oldIndex = bestPath.get(i);
            Integer newIndex = getNewIndex(oldIndex, oldVertices, vertices);
            if (newIndex != null) {
                bestPath.set(i, newIndex);
            }
        }

        System.out.println("State after deletion:");
        System.out.println("Updated vertices: " + vertices);
        System.out.println("Updated best path: " + bestPath);
        System.out.println("Graph size: " + g.getNbVertices());

        return bestPath;
    }

    private void updateIndexMappings(List<Integer> oldVertices, List<Integer> newVertices) {
        originalToCurrentIndexMap.clear();
        currentToOriginalIndexMap.clear();

        for (int i = 0; i < newVertices.size(); i++) {
            Integer vertex = newVertices.get(i);
            int oldIndex = oldVertices.indexOf(vertex);
            if (oldIndex != -1) {
                originalToCurrentIndexMap.put(oldIndex, i);
                currentToOriginalIndexMap.put(i, oldIndex);
            }
        }
    }


    private Integer getNewIndex(Integer oldIndex, List<Integer> oldVertices, List<Integer> newVertices) {
        if (oldIndex == null) return null;
        Integer vertex = oldIndex < oldVertices.size() ? oldVertices.get(oldIndex) : null;
        if (vertex == null) return null;
        return newVertices.indexOf(vertex);
    }

    /**
     * Returns the best possible path that can be served within the time limit.
     *
     * @return the best possible path as a list of vertices
     */
    public List<Integer> getBestPossiblePath() {
        if (this.bestPath.size()==0)
        {
            this.bestPath = getBestPath();
        }
        int servedPoints = (int) resultPoint.get("served");
        System.out.println("We will serve :" + servedPoints + " points");
        List<Integer> bestPathSubList = bestPath.subList(0, servedPoints + 1);
        if (servedPoints > 0 && bestPathSubList.getFirst() != bestPathSubList.getLast()) {
            bestPathSubList.add(bestPathSubList.getFirst());
        }
        return bestPathSubList;
    }

    public Map<Integer, LocalTime> getPointsWithTime() {
        return (Map<Integer, LocalTime>) resultPoint.get("pointsWithTime");
    }

    /**
     * Returns the best possible cost of serving the points within the time limit.
     *
     * @return the best possible cost
     */
    public double getBestPossibleCost() {

        double cost = (double) resultPoint.get("cost");
        return cost;
    }

    public void computePointsToBeServed() {
        try {
            System.out.println("Initial path in compute: " + this.bestPath);

            // Initialize bestPath if empty
            if (this.bestPath == null || this.bestPath.isEmpty()) {
                this.bestPath = getBestPath();
                System.out.println("Initialized path: " + this.bestPath);
            }

            // Now validate the path when we know it's not empty
            validatePath();

            // Continue with point calculation
            pointsToBeServed();

        } catch (Exception e) {
            System.err.println("Error computing points to be served: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    private void pointsToBeServed() {
        System.out.println("Computing times for path: " + this.bestPath);

        Map<Integer, LocalTime> pointsWithTime = new HashMap<>();
        double currentCost = 0.0;
        LocalTime currentTime = LocalTime.of(8, 0);
        double speed = 15.0;
        double serviceTimeInSeconds = 5.0 * 60.0;
        double timeLimitInSeconds = 8.0 * 60.0 * 60.0;
        int pathSize = this.bestPath.size();

        System.out.println("Computing times for path: " + this.bestPath);

        // Add the departure time for the starting point
        pointsWithTime.put(this.bestPath.get(0), currentTime);

        double totalTimeInSeconds = 0.0;

        for (int i = 0; i < pathSize - 1; i++) {
            int currentPosition = this.bestPath.get(i);
            int nextPosition = this.bestPath.get(i + 1);

            // Get the actual distance between points using their indices in the graph
            double distanceMeters = g.getCost(vertices.indexOf(currentPosition),
                    vertices.indexOf(nextPosition));

            // Convert the distance to kilometers
            double distanceKm = distanceMeters / 1000.0;

            // Calculate travel time in seconds
            // speed is in km/h, so multiply by 3600 to get seconds
            double travelTimeSeconds = (distanceKm / speed) * 3600.0;

            // Add service time (5 minutes = 300 seconds) if it's not the last point
            // and if it's not the starting point (i > 0)
            if (i > 0) {
                totalTimeInSeconds += serviceTimeInSeconds;
                currentTime = currentTime.plusSeconds((long)serviceTimeInSeconds);
            }

            // Add travel time
            totalTimeInSeconds += travelTimeSeconds;
            currentTime = currentTime.plusSeconds((long)travelTimeSeconds);

            System.out.printf("From %d to %d: distance=%.2fm, time=%.2fs, arrival=%s%n",
                    currentPosition, nextPosition, distanceMeters, travelTimeSeconds, currentTime);

            // Check if the time limit is exceeded
            if (totalTimeInSeconds > timeLimitInSeconds) {
                System.out.println("Time limit exceeded after point " + currentPosition);
                break;
            }

            currentCost += distanceMeters;
            pointsWithTime.put(nextPosition, currentTime);
        }

        resultPoint.put("served", pathSize - 1);
        resultPoint.put("cost", currentCost);
        resultPoint.put("pointsWithTime", pointsWithTime);

        System.out.println("Final arrival times: " + pointsWithTime);
    }
    public boolean getTimeExceeded()
    {
        return solvingStrategy.getTimeExceeded();
    }
}

