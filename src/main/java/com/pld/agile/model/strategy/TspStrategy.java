package com.pld.agile.model.strategy;

import com.pld.agile.model.graph.CompleteGraph;
import com.pld.agile.model.graph.Graph;
import com.pld.agile.model.algorithm.tsp.TSP;
import com.pld.agile.model.algorithm.tsp.TSP1;

import java.util.ArrayList;
import java.util.List;

public class TspStrategy implements SolvingStrategy {
    private TSP tsp;
    private List<Integer> bestPath;
    private double bestCost;

    public TspStrategy() {
        this.tsp = new TSP1();
        this.bestPath = new ArrayList<Integer>();
    }

    @Override
    public void solve(ArrayList<ArrayList<Double>> completeMatrix) {
        int nbVertices = completeMatrix.size(); //TODO verifier
        Graph g = new CompleteGraph(nbVertices, completeMatrix);
        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, g);
        System.out.print("Solution of cost " + tsp.getSolutionCost() + " found in "
                + (System.currentTimeMillis() - startTime) + "ms : ");
        for (int i = 0; i < nbVertices; i++)
            System.out.print(tsp.getSolution(i) + " ");
        System.out.println("0");
    }

    @Override
    public List<Integer> getBestPath() {
        return bestPath;
    }

    @Override
    public double getBestCost() {
        return bestCost;
    }
}
