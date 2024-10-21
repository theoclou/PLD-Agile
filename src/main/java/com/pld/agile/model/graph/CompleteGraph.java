package com.pld.agile.model.graph;

import java.util.ArrayList;

public class CompleteGraph implements Graph {
	int nbVertices;

	ArrayList<ArrayList<Double>> cost = new ArrayList<>();

	/**
	 * Create a complete directed graph such that each edge has a weight within
	 * [MIN_COST,MAX_COST]
	 * 
	 * @param nbVertices
	 */
	public CompleteGraph(int nbVertices,ArrayList<ArrayList<Double>> cost) {
		this.nbVertices = nbVertices;
		this.cost =cost;
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public Double getCost(int i, int j) {
		if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices)
			return -1.0;
		return cost.get(i).get(j);
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices)
			return false;
		return i != j;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Number of Vertices: ").append(nbVertices).append("\nCost Matrix:\n");

		for (int i = 0; i < nbVertices; i++) {
			for (int j = 0; j < nbVertices; j++) {
				if (cost.get(i).get(j) == -1) {
					sb.append(" ∞ "); // Use infinity symbol for self-loops or non-existent connections
				} else {
					sb.append(String.format("%4d", cost.get(i).get(j))).append(" ");
				}
			}
			sb.append("\n");
		}

		return sb.toString();
	}

}
