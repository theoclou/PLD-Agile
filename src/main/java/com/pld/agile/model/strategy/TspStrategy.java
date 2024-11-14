package com.pld.agile.model.strategy;

import com.pld.agile.model.graph.CompleteGraph;
import com.pld.agile.model.graph.Graph;
import com.pld.agile.model.algorithm.tsp.TSP;
import com.pld.agile.model.algorithm.tsp.TSP1;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code TspStrategy} class implements the {@code SolvingStrategy} interface
 * and solves the Traveling Salesman Problem (TSP) using a TSP solver. This class
 * follows the strategy pattern for solving the TSP and uses a {@code TSP} solver
 * to find the optimal path and cost.
 */
public class TspStrategy implements SolvingStrategy {
    private TSP tsp;
    private List<Integer> bestPath;
    private double bestCost;

    /**
     * Constructs a new {@code TspStrategy} instance, initializing the {@code TSP} solver
     * and setting up an empty list to store the best path.
     */
    public TspStrategy() {
        this.tsp = new TSP1();  // Initializing with a specific TSP implementation
        this.bestPath = new ArrayList<Integer>();
    }

    /**
     * Solves the Traveling Salesman Problem (TSP) using a provided cost matrix
     * that represents the complete graph. The solution is computed using the
     * TSP solver, and the best path and cost are stored.
     *
     * @param g the complete graph 
     */
    @Override
    public void solve(CompleteGraph g) {
        // int nbVertices = completeGraph.size();  // Get the number of vertices in the graph
        // Graph g = new CompleteGraph(nbVertices, completeGraph);  // Create a graph from the cost matrix
        long startTime = System.currentTimeMillis();  // Start time tracking for the solution process
        tsp.searchSolution(20000, g);  // Run the TSP solver with a limit of 20,000 iterations

        // Print the solution cost and the time taken
        System.out.print("Clsassic solver solution of cost " + tsp.getSolutionCost() + " found in "
                + (System.currentTimeMillis() - startTime) + "ms : ");

        // Retrieve the solution path and store it in the bestPath list
        for (int i = 0; i < g.getNbVertices(); i++) {
            int iSol = tsp.getSolution(i);
            System.out.print(iSol + " ");
            bestPath.add(iSol);
        }
        System.out.println("0");  // Print the return to the starting point
    }

    /**
     * Returns the best path found by the TSP solver.
     *
     * @return the best path as a {@code List<Integer>} of vertex indices
     */
    @Override
    public List<Integer> getBestPath() {
        bestPath.add(bestPath.get(0));
        return bestPath;
    }

    /**
     * Returns the best cost found by the TSP solver.
     *
     * @return the best cost as a {@code double} value
     */
    @Override
    public double getBestCost() {
        return bestCost;
    }
    public boolean getTimeExceeded()
    {
        return false;
    }
}
