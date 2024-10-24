package com.pld.agile.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pld.agile.model.graph.CompleteGraph;
import com.pld.agile.model.graph.Plan;
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
        int size = vertices.size();
        System.out.printf("i=%d", vertices.size());
        for (int i = 0; i < size; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    row.add(-1.0); // Distance to self is set to -1
                } else {
                    Double distance = plan.findShortestDistance(vertices.get(i), vertices.get(j));
                    row.add(distance); // Add distance between vertices
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
        return solvingStrategy.getBestPath();
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

    /**
     * Returns the best possible path that can be served within the time limit.
     *
     * @return the best possible path as a list of vertices
     */
    public List<Integer> getBestPossiblePath() {
        List<Integer> bestPath = getBestPath();
        int servedPoints = (int) pointsToBeServed().get("served");
        List<Integer> bestPathSubList = bestPath.subList(0, servedPoints + 1);
        return bestPathSubList;
    }

    /**
     * Returns the best possible cost of serving the points within the time limit.
     *
     * @return the best possible cost
     */
    public double getBestPossibleCost() {
        double cost = (double) pointsToBeServed().get("cost");
        return cost;
    }

    /**
     * Determines how many points can be served and the cost within a given time
     * limit (8 hours).
     *
     * @return a map containing the number of served points and the total cost
     */
    private Map<String, Object> pointsToBeServed() {
        List<Integer> bestPath = getBestPath();
        double currentCost = 0;
        int servedPoints = 0;
        double speed = 1500.0;
        double possibleCost = 0;
        while (currentCost / speed + servedPoints / 12.0 < 8 && servedPoints < bestPath.size()) {
            int currentPosition = bestPath.get(servedPoints);
            int nextPosition = bestPath.get(servedPoints + 1);
            currentCost += g.getCost(currentPosition, nextPosition);
            if (currentCost / speed + servedPoints / 12.0 < 8 && servedPoints < bestPath.size() - 1) {
                servedPoints += 1;
            }
            possibleCost = currentCost;
        }
        Map<String, Object> points = new HashMap<>();
        points.put("served", servedPoints);
        points.put("cost", possibleCost);
        return points;
    }
}
