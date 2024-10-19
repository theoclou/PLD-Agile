package com.pld.agile.model;

import java.util.*;

public class Graph {
    private Plan<Intersection, List<Section>> adjacencyMatrixTSP;

    public Graph() {}

    public Graph init(Plan<Intersection, List<Section>> adjacencyMatrixTSP){
        this.adjacencyMatrixTSP = adjacencyMatrixTSP;
        return this;
    }

    public List<Section> TSPSolver(Set<Intersection> listeIntersection){
        // TODO
        List<Section> result = new ArrayList<Section>();
        return result;
    }

    public List<Section> optimizedTSPSolver(Set<Intersection> listeIntersection){
        // TODO
        List<Section> result = new ArrayList<Section>();
        return result;
    }

    public List<List<Section>> createMatrix(Plan map){
        //TODO
        // IDK what the return type is
        List<List<Section>> result = new ArrayList<>();
        return result;
    }

}
