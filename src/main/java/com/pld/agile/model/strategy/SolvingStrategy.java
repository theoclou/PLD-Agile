package com.pld.agile.model.strategy;

import java.util.List;

import com.pld.agile.model.graph.CompleteGraph;

/**
 * The {@code SolvingStrategy} interface defines the contract for strategies that solve
 * the Traveling Salesman Problem (TSP). Different implementations of this interface
 * can use various algorithms to solve the problem and provide the best path and cost.
 */
public interface SolvingStrategy {

    /**
     * Solves the Traveling Salesman Problem (TSP) based on a provided graph represented
     * as a cost matrix. This method will compute the best path and cost using the
     * strategy's algorithm.
     *
     * @param graph the complete graph 
     */
    void solve(CompleteGraph graph);

    /**
     * Returns the best path found by the solver after solving the TSP.
     *
     * @return the best path as a {@code List<Integer>} of vertex indices
     */
    List<Integer> getBestPath();

    /**
     * Returns the best cost found by the solver after solving the TSP.
     *
     * @return the best cost as a {@code double} value
     */
    double getBestCost();
}
