package com.pld.agile.model;

import java.util.*;

public class Graph {
    private Map<Intersection, List<Section>> adjacencyMatrixTSP;

    public Graph() {}

    public Graph init(Map<Intersection, List<Section>> adjacencyMatrixTSP){
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

    public List<List<Section>> createMatrix(Plan plan){
        //TODO
        // IDK what the return type is
        List<List<Section>> result = new ArrayList<>();
        return result;
    }

}

