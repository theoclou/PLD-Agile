package com.pld.agile.model.algorithm.tsp;

import com.pld.agile.model.graph.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * The {@code TemplateTSP} class provides an abstract template for solving the
 * Traveling Salesman Problem (TSP) using a branch and bound algorithm. Subclasses
 * of {@code TemplateTSP} must define specific methods for calculating lower bounds
 * and determining the traversal order of vertices.
 */
public abstract class TemplateTSP implements TSP {
	private Integer[] bestSol;
	protected Graph g;
	private Double bestSolCost;
	private int timeLimit;
	private long startTime;

	/**
	 * Starts the search for the best solution to the TSP using a branch and bound algorithm.
	 * The method runs within the given time limit and attempts to find the shortest
	 * possible route that visits each vertex exactly once and returns to the starting vertex.
	 *
	 * @param timeLimit the maximum time allowed for the search (in milliseconds)
	 * @param g         the graph representing the TSP problem
	 */
	public void searchSolution(int timeLimit, Graph g) {
		if (timeLimit <= 0)
			return;
		startTime = System.currentTimeMillis();
		this.timeLimit = timeLimit;
		this.g = g;
		bestSol = new Integer[g.getNbVertices()];
		Collection<Integer> unvisited = new ArrayList<>(g.getNbVertices() - 1);
		for (int i = 1; i < g.getNbVertices(); i++)
			unvisited.add(i);
		Collection<Integer> visited = new ArrayList<>(g.getNbVertices());
		visited.add(0); // The first visited vertex is 0
		bestSolCost = Double.MAX_VALUE;
		branchAndBound(0, unvisited, visited, 0.0);
	}

	/**
	 * Returns the vertex in the best solution at the given index.
	 *
	 * @param i the index of the vertex in the best solution
	 * @return the vertex at index {@code i}, or -1 if no valid solution exists
	 */
	public Integer getSolution(int i) {
		if (g != null && i >= 0 && i < g.getNbVertices())
			return bestSol[i];
		return -1;
	}

	/**
	 * Returns the cost of the best solution found.
	 *
	 * @return the total cost of the best solution, or -1.0 if no solution was found
	 */
	public Double getSolutionCost() {
		if (g != null)
			return bestSolCost;
		return -1.0;
	}

	/**
	 * Method that must be implemented in subclasses of {@code TemplateTSP}.
	 * Computes a lower bound on the cost of completing the tour from the current vertex
	 * through the remaining unvisited vertices and back to the starting vertex.
	 *
	 * @param currentVertex the current vertex
	 * @param unvisited     the set of unvisited vertices
	 * @return a lower bound on the remaining cost of the tour
	 */
	protected abstract int bound(Integer currentVertex, Collection<Integer> unvisited);

	/**
	 * Method that must be implemented in subclasses of {@code TemplateTSP}.
	 * Provides an iterator for traversing the unvisited vertices that are successors
	 * of the current vertex in the graph. The iterator determines the order in which
	 * these vertices are explored during the branch and bound search.
	 *
	 * @param currentVertex the current vertex
	 * @param unvisited     the set of unvisited vertices
	 * @param g             the graph representing the problem
	 * @return an iterator for the unvisited successors of {@code currentVertex}
	 */
	protected abstract Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g);

	/**
	 * The core branch and bound algorithm for solving the TSP.
	 * This method recursively explores possible solutions by branching on unvisited vertices
	 * and pruning paths that exceed the current best solution using lower bounds.
	 *
	 * @param currentVertex the last visited vertex
	 * @param unvisited     the set of vertices that have not yet been visited
	 * @param visited       the sequence of vertices that have already been visited
	 * @param currentCost   the current cost of the path corresponding to {@code visited}
	 */
	private void branchAndBound(int currentVertex, Collection<Integer> unvisited,
								Collection<Integer> visited, Double currentCost) {
		if (System.currentTimeMillis() - startTime > timeLimit)
			return;
		if (unvisited.isEmpty()) {
			if (g.isArc(currentVertex, 0)) {
				if (currentCost + g.getCost(currentVertex, 0) < bestSolCost) {
					visited.toArray(bestSol);
					bestSolCost = currentCost + g.getCost(currentVertex, 0);
				}
			}
		} else if (currentCost + bound(currentVertex, unvisited) < bestSolCost) {
			Iterator<Integer> it = iterator(currentVertex, unvisited, g);
			while (it.hasNext()) {
				Integer nextVertex = it.next();
				visited.add(nextVertex);
				unvisited.remove(nextVertex);
				branchAndBound(nextVertex, unvisited, visited,
						currentCost + g.getCost(currentVertex, nextVertex));
				visited.remove(nextVertex);
				unvisited.add(nextVertex);
			}
		}
	}
}
