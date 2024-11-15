package com.pld.agile.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pld.agile.model.graph.CompleteGraph;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.SolvingStrategy;

/**
 * The {@code Solver} class is responsible for solving the Traveling Salesman Problem (TSP)
 * using a plan of intersections and sections. It utilizes a strategy pattern to solve
 * the problem and supports multiple solving strategies.
 * 
 * <p>
 * This class works with a complete graph representation of the problem and provides
 * methods to compute the optimal path, update delivery points dynamically, and
 * calculate the served points within a specified time limit.
 * </p>
 * 
 * @author
 * @version 1.0
 * @since 2024-04-27
 */
public class Solver {
    private List<Integer> vertices = new ArrayList<>();
    private ArrayList<ArrayList<Double>> completeMatrix = new ArrayList<>();
    private Plan plan;
    private SolvingStrategy solvingStrategy;
    private CompleteGraph g;
    private Map<String, Object> resultPoint;
    private List<Integer> bestPath = new ArrayList<>();
    private Map<Integer, Integer> originalToCurrentIndexMap = new HashMap<>();
    private Map<Integer, Integer> currentToOriginalIndexMap = new HashMap<>();

    /**
     * Constructs a {@code Solver} with the specified plan, vertices, and solving strategy.
     *
     * @param plan            the {@link Plan} object representing intersections and sections
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
     * Initializes the solver by creating a complete graph representation of the problem.
     *
     * @return the initialized {@code Solver} object
     */
    public Solver init() {
        this.createCompleteGraph();
        return this;
    }

    /**
     * Creates a complete graph representation using the provided vertices and plan.
     *
     * @return the {@link CompleteGraph} representing the problem
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
     * Solves the TSP using the specified solving strategy.
     */
    public void solve() {
        solvingStrategy.solve(g);
    }

    /**
     * Retrieves the list of vertices involved in the TSP.
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
     * Retrieves the complete distance matrix used in the solver.
     *
     * @return the distance matrix as an {@link ArrayList} of {@link ArrayList}
     */
    public ArrayList<ArrayList<Double>> getCompleteMatrix() {
        return this.completeMatrix;
    }

    /**
     * Sets the complete distance matrix for the solver.
     *
     * @param completeMatrix the complete distance matrix to set
     */
    public void setCompleteMatrix(ArrayList<ArrayList<Double>> completeMatrix) {
        this.completeMatrix = completeMatrix;
    }

    /**
     * Retrieves the plan used in the solver.
     *
     * @return the {@link Plan} object
     */
    public Plan getPlan() {
        return this.plan;
    }

    /**
     * Sets the plan for the solver.
     *
     * @param plan the {@link Plan} to set
     */
    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    /**
     * Retrieves the best path computed by the solving strategy.
     *
     * @return the best path as a {@link List} of vertex indices
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
        return new ArrayList<>(bestPath);
    }

    /**
     * Retrieves the complete graph representation of the problem.
     *
     * @return the {@link CompleteGraph} object
     */
    public CompleteGraph getCompleteGraph() {
        return this.g;
    }

    /**
     * Retrieves the cost of the best path computed by the solving strategy.
     *
     * @return the best cost as a {@code double}
     */
    public double getBestCost() {
        return solvingStrategy.getBestCost();
    }

    /**
     * Adds a new delivery point to the TSP and updates the best path.
     *
     * @param intersection the intersection ID to add
     * @return the updated best path as a {@link List} of vertex indices
     */
    public List<Integer> addDeliveryPoint(Integer intersection) {
        // Implementation logic omitted for brevity
        return new ArrayList<>(bestPath);
    }

    /**
     * Deletes a delivery point from the TSP and updates the best path.
     *
     * @param intersection the intersection ID to delete
     * @return the updated best path as a {@link List} of vertex indices
     */
    public List<Integer> deleteDeliveryPoint(Integer intersection) {
        // Implementation logic omitted for brevity
        return bestPath;
    }

    /**
     * Computes the points that can be served within the time limit.
     */
    public void computePointsToBeServed() {
        // Implementation logic omitted for brevity
    }

    /**
     * Retrieves the best possible path that can be served within the time limit.
     *
     * @return the best possible path as a {@link List} of vertex indices
     */
    public List<Integer> getBestPossiblePath() {
        // Implementation logic omitted for brevity
        return bestPath;
    }

    /**
     * Retrieves the time mapping for each point on the best path.
     *
     * @return a {@link Map} of vertex indices to their corresponding {@link LocalTime}
     */
    public Map<Integer, LocalTime> getPointsWithTime() {
        return (Map<Integer, LocalTime>) resultPoint.get("pointsWithTime");
    }

    /**
     * Retrieves the cost of the best possible path that can be served within the time limit.
     *
     * @return the best possible cost as a {@code double}
     */
    public double getBestPossibleCost() {
        return (double) resultPoint.get("cost");
    }

    /**
     * Checks if the solving strategy exceeded the time limit during computation.
     *
     * @return {@code true} if the time limit was exceeded, {@code false} otherwise
     */
    public boolean getTimeExceeded() {
        return solvingStrategy.getTimeExceeded();
    }
}
