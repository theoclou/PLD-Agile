package com.pld.agile.model.algorithm.tsp;

import com.pld.agile.model.graph.Graph;

public interface TSP {

	/**
	 * Search for a shortest cost hamiltonian circuit in <code>g</code> within <code>timeLimit</code> milliseconds
	 * (returns the best found tour whenever the time limit is reached)
	 * Warning: The computed tour always start from vertex 0
	 * @param timeLimit
	 * @param g
	 */
	public void searchSolution(int timeLimit, Graph g);
	
	/**
	 * @param i
	 * @return the ith visited vertex in the solution of tsp
	 */
	public Integer getSolution(int i);
	
	/** 
	 * @return the total cost of the solution computed by <code>searchSolution</code> 
	 * (-1 if <code>searcheSolution</code> has not been called yet).
	 */
	public Double getSolutionCost();

}
