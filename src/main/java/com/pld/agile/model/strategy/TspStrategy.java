package com.pld.agile.model.strategy;

import java.util.ArrayList;
import java.util.List;

import com.pld.agile.model.algorithm.tsp.TSP;
import com.pld.agile.model.algorithm.tsp.TSP1;
import com.pld.agile.model.graph.CompleteGraph;

/**
 * The {@code TspStrategy} class implements the {@link SolvingStrategy} interface
 * and provides a strategy for solving the Traveling Salesman Problem (TSP) using a
 * specific TSP solver.
 * 
 * <p>
 * This class leverages the {@link TSP} algorithm to compute the optimal solution
 * for the TSP, including the best path and the associated cost. It follows the
 * strategy design pattern to provide flexibility in the choice of solving
 * algorithms.
 * </p>
 * 
 * @author
 * @version 1.0
 * @since 2024-04-27
 */
public class TspStrategy implements SolvingStrategy {
    private TSP tsp;
    private List<Integer> bestPath;
    private double bestCost;

    /**
     * Constructs a new {@code TspStrategy} instance, initializing the {@code TSP} solver
     * with a default implementation ({@link TSP1}) and preparing an empty list to
     * store the best path.
     */
    public TspStrategy() {
        this.tsp = new TSP1(); // Initializing with a specific TSP implementation
        this.bestPath = new ArrayList<>();
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using a given complete graph.
     * 
     * <p>
     * This method employs the {@link TSP} solver to compute the optimal solution
     * for the TSP within a fixed iteration limit. The best path and cost are then
     * stored for retrieval.
     * </p>
     *
     * @param g the {@link CompleteGraph} representing the cost matrix of the graph
     */
    @Override
    public void solve(CompleteGraph g) {
        long startTime = System.currentTimeMillis(); // Start time tracking
        tsp.searchSolution(20000, g); // Solve with an iteration limit of 20,000

        // Log the solution details
        System.out.print("Classic solver solution of cost " + tsp.getSolutionCost() + " found in "
                + (System.currentTimeMillis() - startTime) + "ms: ");

        // Retrieve and store the solution path
        for (int i = 0; i < g.getNbVertices(); i++) {
            int iSol = tsp.getSolution(i);
            System.out.print(iSol + " ");
            bestPath.add(iSol);
        }
        System.out.println("0"); // Return to the starting point
    }

    /**
     * Retrieves the best path computed by the TSP solver.
     * 
     * <p>
     * The best path represents the optimal order of vertices to visit to minimize
     * the total travel cost. The path is a closed loop, returning to the starting
     * point.
     * </p>
     *
     * @return the best path as a {@code List<Integer>} of vertex indices
     */
    @Override
    public List<Integer> getBestPath() {
        bestPath.add(bestPath.get(0)); // Ensure the path loops back to the start
        return bestPath;
    }

    /**
     * Retrieves the best cost associated with the best path computed by the TSP solver.
     * 
     * <p>
     * The best cost represents the total cost of the optimal route as determined
     * by the solver.
     * </p>
     *
     * @return the best cost as a {@code double} value
     */
    @Override
    public double getBestCost() {
        return bestCost;
    }

    /**
     * Indicates whether the solving process exceeded the time limit.
     * 
     * <p>
     * This implementation always returns {@code false} as the TSP solver in this
     * strategy does not include time-based termination.
     * </p>
     *
     * @return {@code false} to indicate that no time limit was exceeded
     */
    @Override
    public boolean getTimeExceeded() {
        return false;
    }
}
