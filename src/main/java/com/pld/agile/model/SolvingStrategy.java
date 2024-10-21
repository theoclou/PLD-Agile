package com.pld.agile.model;

import java.util.ArrayList;
import java.util.List;

public interface SolvingStrategy {
    void solve(ArrayList<ArrayList<Double>> graph); //TODO MAJ des bestPath et bestCost dans le solve
    List<Integer> getBestPath();
    double getBestCost();
}
