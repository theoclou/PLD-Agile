package com.pld.agile.model;

import java.util.ArrayList;
import java.util.List;

import com.pld.agile.model.tsp.CompleteGraph;
import com.pld.agile.model.tsp.Graph;
import com.pld.agile.model.tsp.TSP;

public class Solver {
    // private Plan<Intersection, ArrayList<Section>> adjacencyMatrixTSP;
    private List<Integer> vertices = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<ArrayList<Double>> completeMatrix = new ArrayList<>();
    private Plan plan;
    private TSP tsp;

    public Solver(Plan plan, List<Integer> vertices,TSP tsp) {
        this.plan = new Plan();
        this.plan = plan;
        this.vertices = vertices;
        this.tsp=tsp;
    }

    // fills the matrix with values to create a complete graph

    public void createCompleteGraph() {
        int size = vertices.size();
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
    public void solveTSP()
    {
        int nbVertices=vertices.size();
        Graph g = new CompleteGraph(nbVertices,completeMatrix);
			long startTime = System.currentTimeMillis();
			tsp.searchSolution(20000, g);
			System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
					+(System.currentTimeMillis() - startTime)+"ms : ");
			for (int i=0; i<nbVertices; i++)
				System.out.print(tsp.getSolution(i)+" ");
			System.out.println("0");
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

    public TSP getTsp() {
        return this.tsp;
    }

    public void setTsp(TSP tsp) {
        this.tsp = tsp;
    }

}
