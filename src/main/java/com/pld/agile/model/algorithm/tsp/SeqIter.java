package com.pld.agile.model.algorithm.tsp;


import com.pld.agile.model.graph.Graph;

import java.util.Collection;
import java.util.Iterator;

import java.util.Collection;
import java.util.Iterator;

/**
 * The {@code SeqIter} class implements an {@code Iterator} for traversing the set of
 * unvisited vertices that are successors of a given current vertex in a graph.
 * The vertices are traversed in the same order as they appear in the unvisited set.
 */
public class SeqIter implements Iterator<Integer> {
	private Integer[] candidates;
	private int nbCandidates;

	/**
	 * Creates an iterator to traverse the set of vertices in {@code unvisited}
	 * that are successors of {@code currentVertex} in the graph {@code g}.
	 * Vertices are traversed in the same order as in {@code unvisited}.
	 *
	 * @param unvisited      the set of unvisited vertices
	 * @param currentVertex  the current vertex from which successors are considered
	 * @param g              the {@code Graph} object representing the graph
	 */
	public SeqIter(Collection<Integer> unvisited, int currentVertex, Graph g) {
		this.candidates = new Integer[unvisited.size()];
		for (Integer s : unvisited) {
			if (g.isArc(currentVertex, s)) {
				candidates[nbCandidates++] = s;
			}
		}
	}

	/**
	 * Checks if there are more candidates (successors) to iterate over.
	 *
	 * @return {@code true} if there are more candidates, {@code false} otherwise
	 */
	@Override
	public boolean hasNext() {
		return nbCandidates > 0;
	}

	/**
	 * Returns the next candidate (successor) in the iteration.
	 *
	 * @return the next successor vertex as an {@code Integer}
	 */
	@Override
	public Integer next() {
		nbCandidates--;
		return candidates[nbCandidates];
	}

	/**
	 * This method is not supported for this iterator.
	 * Calling this method will have no effect.
	 */
	@Override
	public void remove() {
		// Not supported
	}
}
