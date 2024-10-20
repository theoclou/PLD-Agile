package com.pld.agile.model.tspOptimized;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class runBnB {
    public static void main(String[] args) {

        int n = 40;

        long t = System.currentTimeMillis();
        ArrayList<Integer> visited = new ArrayList<>();
        visited.add(0);

        // Create the initial notVisited set with vertices 1 to n-1
        Set<Integer> notVisited = new HashSet<>();
        for (int i = 1; i < n; i++) {
            notVisited.add(i);
        }
        BranchAndBound bnb = new BranchAndBound();
        Double distance = 0.0;
        // bnb.finBestCost(visited, notVisited, distance);
        System.out.printf("n=%d nbCalls=%d time=%.3fs\n", n, bnb.getNbCalls(),
                (System.currentTimeMillis() - t) / 1000.0);

    }
}
