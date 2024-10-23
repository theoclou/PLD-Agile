package com.pld.agile.model.algorithm.tsp;

import com.pld.agile.model.graph.Graph;

import java.util.Collection;
import java.util.Iterator;

import java.util.Collection;
import java.util.Iterator;
import com.pld.agile.model.graph.Graph;

/**
 * The {@code TSP1} class extends the {@code TemplateTSP} class and provides
 * a concrete implementation of the Traveling Salesman Problem (TSP) solver
 * using a trivial bound and a basic iterator for unvisited vertices.
 *
 * This class overrides the required methods for calculating a bound and
 * providing an iterator for unvisited vertices.
 */
public class TSP1 extends TemplateTSP {

	/**
	 * Returns a trivial bound of 0 for the remaining cost from the current vertex.
	 *
	 * This implementation does not calculate a meaningful lower bound and simply
	 * returns 0, meaning that no pruning will occur based on the cost.
	 *
	 * @param currentVertex the current vertex in the TSP tour
	 * @param unvisited     the collection of unvisited vertices
	 * @return a lower bound of 0 for the remaining cost
	 */
	@Override
	protected int bound(Integer currentVertex, Collection<Integer> unvisited) {
		return 0;
	}

	/**
	 * Returns an iterator for traversing the unvisited vertices that are successors
	 * of the current vertex in the graph. The vertices are iterated in the order
	 * they appear in the unvisited set.
	 *
	 * @param currentVertex the current vertex in the TSP tour
	 * @param unvisited     the collection of unvisited vertices
	 * @param g             the graph representing the TSP
	 * @return an iterator for the unvisited successors of the current vertex
	 */
	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g) {
		return new SeqIter(unvisited, currentVertex, g);
	}
}
