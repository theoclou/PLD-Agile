package com.pld.agile.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.SolvingStrategy;

public class Solver {
    // private Plan<Intersection, ArrayList<Section>> adjacencyMatrixTSP;
    private List<Integer> vertices = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<ArrayList<Double>> completeMatrix = new ArrayList<>();
    private Plan plan;
    private SolvingStrategy solvingStrategy;

    public Solver(Plan plan, List<Integer> vertices, SolvingStrategy solvingStrategy) {
        this.plan = plan;
        this.vertices = vertices;
        this.solvingStrategy = solvingStrategy;
    }

    public Solver init() {
        this.createCompleteGraph();
        return this;
    }

    // fills the matrix with values to create a complete graph
    public void createCompleteGraph() {
        int size = vertices.size();
        System.out.printf("i=%d", vertices.size());
        for (int i = 0; i < size; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    row.add(-1.0); // Distance to self is set to -1
                } else {
                    Double distance = plan.findShortestDistance(vertices.get(i), vertices.get(j));
                    row.add(distance); // Initialize with a large value to indicate no direct connection
                }
            }
            completeMatrix.add(row);
        }
    }

    public void solve() {
        solvingStrategy.solve(completeMatrix);
    }

    public List<Integer> getVertices() {
        return this.vertices;
    }

    public void setVertices(List<Integer> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<ArrayList<Double>> getCompleteMatrix() {
        return this.completeMatrix;
    }

    public void setCompleteMatrix(ArrayList<ArrayList<Double>> completeMatrix) {
        this.completeMatrix = completeMatrix;
    }

    public Plan getPlan() {
        return this.plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public List<Integer> getBestPath() {
        return solvingStrategy.getBestPath();
    }

    public double getBestCost() {
        return solvingStrategy.getBestCost();
    }

    public List<Integer> getBestPossiblePath() {
        List<Integer> bestPath = getBestPath();
        int servedPoints = (int) pointsToBeServed().get("served");
        List<Integer> bestPathSubList = bestPath.subList(0, servedPoints + 1);
        return bestPathSubList;
    }

    public double getBestPossibleCost() {
        double cost = (double) pointsToBeServed().get("cost");
        return cost;
    }

    private Map<String, Object> pointsToBeServed() {
        List<Integer> bestPath = getBestPath();
        double currentCost = 0;
        int servedPoints = 0;
        double speed = 1500.0;
        double possibleCost = 0;
        while (currentCost / speed + servedPoints / 12.0 < 8 && servedPoints < bestPath.size() - 1) {
            int currentPosition = bestPath.get(servedPoints);
            int nextPosition = bestPath.get(servedPoints + 1);
            currentCost += completeMatrix.get(currentPosition).get(nextPosition);
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
