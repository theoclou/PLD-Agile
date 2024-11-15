package com.pld.agile.model.strategy;

import java.util.List;

import com.pld.agile.model.graph.CompleteGraph;

/**
 * The {@code SolvingStrategy} interface defines the contract for solving 
 * the Traveling Salesman Problem (TSP) using different algorithms. 
 * 
 * <p>
 * Implementations of this interface should provide methods to solve the TSP, 
 * retrieve the best path, and retrieve the cost of the optimal solution.
 * </p>
 * 
 * <p>
 * This interface uses a {@link CompleteGraph} object to represent the problem 
 * as a graph where each vertex is connected to every other vertex with a cost 
 * (or distance) associated with each edge.
 * </p>
 * 
 * @author 
 * @version 1.0
 * @since 2024-04-27
 */
public interface SolvingStrategy {

    /**
     * Solves the Traveling Salesman Problem (TSP) based on the provided graph.
     * 
     * <p>
     * Implementing classes will use their specific algorithm to compute the 
     * optimal solution, including the best path and cost.
     * </p>
     *
     * @param graph the {@link CompleteGraph} representing the problem as a cost matrix
     */
    void solve(CompleteGraph graph);

    /**
     * Retrieves the best path found by the strategy after solving the TSP.
     * 
     * <p>
     * The best path is represented as a {@code List<Integer>} of vertex indices, 
     * where each index corresponds to a vertex in the graph.
     * </p>
     *
     * @return the best path as a {@code List<Integer>} of vertex indices
     */
    List<Integer> getBestPath();

    /**
     * Retrieves the best cost found by the strategy after solving the TSP.
     * 
     * <p>
     * The best cost represents the total cost (or distance) of the optimal route 
     * computed for the TSP.
     * </p>
     *
     * @return the best cost as a {@code double} value
     */
    double getBestCost();

    /**
     * Checks whether the solver exceeded the predefined time limit during the computation.
     * 
     * <p>
     * This method allows for monitoring if the solving process was interrupted due 
     * to exceeding a time constraint.
     * </p>
     *
     * @return {@code true} if the time limit was exceeded, {@code false} otherwise
     */
    boolean getTimeExceeded();
}
