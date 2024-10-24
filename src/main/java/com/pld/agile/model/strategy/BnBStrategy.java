package com.pld.agile.model.strategy;

import com.pld.agile.model.algorithm.bnb.BranchAndBound;

import java.util.ArrayList;
import java.util.List;

import com.pld.agile.model.graph.CompleteGraph;
/**
 * The {@code BnBStrategy} class implements the {@code SolvingStrategy} interface and
 * solves the Traveling Salesman Problem (TSP) using the Branch and Bound (BnB) algorithm.
 * This class serves as a strategy in the strategy pattern for solving the TSP.
 */
public class BnBStrategy implements SolvingStrategy {
    private BranchAndBound bnb;
    private List<Integer> bestPath;
    private double bestCost;


    /**
     * Constructs a new {@code BnBStrategy} instance with a fresh {@code BranchAndBound}
     * solver. Initializes the best path to an empty list.
     */
    public BnBStrategy() {
        this.bnb = new BranchAndBound();
        this.bestPath = new ArrayList<Integer>();
    }
    /**
     * Solves the Traveling Salesman Problem (TSP) using the Branch and Bound algorithm.
     * It takes the complete graph represented by a cost matrix, sets it in the
     * {@code BranchAndBound} object, and finds the best cost and path.
     *
     * @param completeMatrix the complete graph represented as a cost matrix, where each
     *                       entry indicates the cost (or distance) between two vertices
     */
    @Override
    public void solve(CompleteGraph completeGraph) {
        bnb.setCompleteGraph(completeGraph);
        bnb.findBestCost();
    }
    /**
     * Returns the best path found by the Branch and Bound algorithm.
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
     * @return the best cost as a {@code double} value
     */
    @Override
    public double getBestCost() {
        return bestCost;
    }
}
