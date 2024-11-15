package com.pld.agile.model.strategy;

import java.util.ArrayList;
import java.util.List;

import com.pld.agile.model.algorithm.bnb.BranchAndBound;
import com.pld.agile.model.graph.CompleteGraph;

/**
 * The {@code BnBStrategy} class implements the {@code SolvingStrategy} interface 
 * and provides a solution to the Traveling Salesman Problem (TSP) using the 
 * Branch and Bound (BnB) algorithm. This class acts as a concrete strategy in the 
 * Strategy Pattern for solving the TSP.
 * 
 * <p>
 * The class utilizes a {@link BranchAndBound} object to compute the optimal path 
 * and cost for the TSP based on a complete graph represented as a cost matrix.
 * </p>
 * 
 * @author 
 * @version 1.0
 * @since 2024-04-27
 */
public class BnBStrategy implements SolvingStrategy {
    private BranchAndBound bnb;
    private List<Integer> bestPath;
    private double bestCost;

    /**
     * Constructs a new {@code BnBStrategy} instance.
     * 
     * <p>
     * This constructor initializes a fresh {@link BranchAndBound} solver and sets 
     * the best path to an empty list.
     * </p>
     */
    public BnBStrategy() {
        this.bnb = new BranchAndBound();
        this.bestPath = new ArrayList<>();
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using the Branch and Bound algorithm.
     * 
     * <p>
     * The method takes a {@link CompleteGraph} object represented by a cost matrix, 
     * sets it in the {@link BranchAndBound} solver, and computes the optimal solution 
     * including the best cost and path.
     * </p>
     *
     * @param completeGraph the complete graph represented as a cost matrix, where 
     *                      each entry indicates the cost (or distance) between two vertices
     */
    @Override
    public void solve(CompleteGraph completeGraph) {
        bnb.setCompleteGraph(completeGraph);
        bnb.findBestCost();
    }

    /**
     * Returns the best path found by the Branch and Bound algorithm.
     * 
     * <p>
     * The best path is represented as a {@code List<Integer>} of vertex indices that 
     * form the optimal route for the TSP.
     * </p>
     *
     * @return the best path as a {@code List<Integer>} of vertex indices
     */
    @Override
    public List<Integer> getBestPath() {
        return bnb.getBestPath();
    }

    /**
     * Returns the best cost found by the Branch and Bound algorithm.
     * 
     * <p>
     * The best cost represents the total cost (or distance) of the optimal route 
     * computed for the TSP.
     * </p>
     *
     * @return the best cost as a {@code double} value
     */
    @Override
    public double getBestCost() {
        return bestCost;
    }

    /**
     * Checks if the time limit for solving the TSP using the Branch and Bound 
     * algorithm was exceeded.
     * 
     * <p>
     * The method queries the {@link BranchAndBound} solver to determine if the 
     * computation was terminated due to exceeding a predefined time limit.
     * </p>
     *
     * @return {@code true} if the time limit was exceeded, {@code false} otherwise
     */
    public boolean getTimeExceeded() {
        return bnb.getTimeExceeded();
    }
}
