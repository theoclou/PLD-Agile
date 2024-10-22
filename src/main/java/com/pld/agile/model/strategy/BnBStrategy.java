package com.pld.agile.model.strategy;

import com.pld.agile.model.algorithm.bnb.BranchAndBound;

import java.util.ArrayList;
import java.util.List;

public class BnBStrategy implements SolvingStrategy {
    private BranchAndBound bnb;
    private List<Integer> bestPath;
    private double bestCost;

    public BnBStrategy() {
        this.bnb = new BranchAndBound();
        this.bestPath = new ArrayList<Integer>();
    }

    @Override
    public void solve(ArrayList<ArrayList<Double>> completeMatrix) {
        bnb.setCostsMatrix(completeMatrix);
        bnb.findBestCost();
    }

    @Override
    public List<Integer> getBestPath() {
        return bestPath;
    }
    
    @Override
    public double getBestCost() {
        return bestCost;
    }
}
