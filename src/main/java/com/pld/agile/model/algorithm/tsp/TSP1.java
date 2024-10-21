package com.pld.agile.model.algorithm.tsp;

import com.pld.agile.model.graph.Graph;

import java.util.Collection;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {
	@Override
	protected int bound(Integer currentVertex, Collection<Integer> unvisited) {
		return 0;
	}

	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g) {
		return new SeqIter(unvisited, currentVertex, g);
	}
}
