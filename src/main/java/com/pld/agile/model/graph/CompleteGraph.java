package com.pld.agile.model.graph;

import java.util.ArrayList;

/**
 * The {@code CompleteGraph} class represents a complete directed graph.
 * Each vertex in the graph is connected to every other vertex, and each
 * edge has a specified cost (weight) that can be retrieved from a cost matrix.
 */
public class CompleteGraph implements Graph {
    int nbVertices;
    ArrayList<ArrayList<Double>> cost = new ArrayList<>();

    /**
     * Constructs a complete directed graph with the specified number of vertices.
     * Each edge has a corresponding weight, which is stored in the provided cost matrix.
     *
     * @param nbVertices the number of vertices in the graph
     * @param cost       the cost matrix representing the weights of the edges between vertices
     */
    public CompleteGraph(int nbVertices, ArrayList<ArrayList<Double>> cost) {
        this.nbVertices = nbVertices;
        this.cost = cost;
    }

    public CompleteGraph() {

    }

    public ArrayList<ArrayList<Double>> getCostMatrix() {
        return this.cost;
    }

    /**
     * Returns the number of vertices in the graph.
     *
     * @return the number of vertices
     */
    @Override
    public int getNbVertices() {
        return nbVertices;
    }

    /**
     * Returns the cost (weight) of the directed edge from vertex {@code i} to vertex {@code j}.
     * If either {@code i} or {@code j} is out of bounds, returns -1.0.
     *
     * @param i the source vertex
     * @param j the destination vertex
     * @return the cost of the edge from {@code i} to {@code j}, or -1.0 if the vertices are out of bounds
     */
    @Override
    public Double getCost(int i, int j) {
        if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices)
            return -1.0;
        return cost.get(i).get(j);
    }

    /**
     * Checks whether an arc (directed edge) exists from vertex {@code i} to vertex {@code j}.
     * <p>
     * In a complete graph, an arc exists between any two distinct vertices. Therefore,
     * this method returns {@code true} if {@code i} and {@code j} are distinct and within bounds.
     *
     * @param i the source vertex
     * @param j the destination vertex
     * @return {@code true} if there is an arc from {@code i} to {@code j}, {@code false} otherwise
     */
    @Override
    public boolean isArc(int i, int j) {
        if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices)
            return false;
        return i != j;
    }

    /**
     * Returns a string representation of the graph, including the number of vertices
     * and the cost matrix. Non-existent connections (self-loops) are represented
     * by the infinity symbol (∞).
     *
     * @return a string representation of the graph
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of Vertices: ").append(nbVertices).append("\nCost Matrix:\n");

        for (int i = 0; i < nbVertices; i++) {
            for (int j = 0; j < nbVertices; j++) {
                if (cost.get(i).get(j) == -1) {
                    sb.append(" ∞ "); // Use infinity symbol for non-existent connections or self-loops
                } else {
                    sb.append(String.format("%4.2f", cost.get(i).get(j))).append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}
