package com.pld.agile.model.algorithm.bnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BranchAndBound {
    private long nbCalls = 0; // Number of calls to the recursive permut function
    private ArrayList<ArrayList<Double>> costsMatrix = new ArrayList<>();
    private double best = Double.MAX_VALUE;
    private int[] bestPath; // To store the best path found

    //  initiate the permutation process
    public void findBestCost() {
        int n = costsMatrix.size(); // Number of nodes
        int[] visited = new int[n + 1]; // +1 to include the return to starting point
        int[] notVisited = new int[n - 1]; // Exclude the starting node (0)

        // Initialize visited and notVisited arrays
        visited[0] = 0; // Start from node 0
        for (int i = 1; i < n; i++) {
            notVisited[i - 1] = i;
        }

        bestPath = new int[n + 1]; // +1 for the return to starting point

        // Start the recursive permutation
        permut(visited, 1, notVisited, n - 1, 0.0);

        // Output the best cost and path
        System.out.println("Best cost: " + best);
        System.out.print("Best path: ");
        for (int i = 0; i <= n; i++) {
            System.out.print(bestPath[i] + " ");
        }
        System.out.println();
    }

    private void permut(int[] visited, int nbVisited, int[] notVisited, int nbNotVisited, double distance) {
        nbCalls++;

        // eliminate paths exceeding current best distance
        if (distance >= best) {
            return;
        }

        // Base case: all nodes have been visited
        if (nbNotVisited == 0) {
            // Add distance to return to the starting point
            distance += costsMatrix.get(visited[nbVisited - 1]).get(0);
            if (distance < best) {
                best = distance;
                // Save the best path found
                System.arraycopy(visited, 0, bestPath, 0, nbVisited);
                bestPath[nbVisited] = 0; // Return to starting point
            }
            return;
        }

        // Create a temporary array for sorting the remaining nodes
        Integer[] sortedNotVisited = new Integer[nbNotVisited];
        for (int i = 0; i < nbNotVisited; i++) {
            sortedNotVisited[i] = notVisited[i];
        }

        int lastVisited = visited[nbVisited - 1];

        // Sort the remaining nodes based on cost from last visited node
        Arrays.sort(sortedNotVisited, Comparator.comparingDouble(a -> costsMatrix.get(lastVisited).get(a)));

        // Iterate over sorted nodes
        for (int idx = 0; idx < nbNotVisited; idx++) {
            int nextNode = sortedNotVisited[idx];
            double newDistance = distance + costsMatrix.get(lastVisited).get(nextNode);

            // Eliminate paths exceeding current best distance
            if (newDistance >= best) {
                continue;
            }

            //intersection check
            boolean hasIntersection = containIntersection(nbVisited, lastVisited, nextNode, visited);
            if (!hasIntersection) {
                // Find the index of nextNode in notVisited
                int indexInNotVisited = findIndex(notVisited, nextNode, nbNotVisited);
                if (indexInNotVisited == -1) {
                    continue; 
                }

                updateTables(visited, nbVisited, notVisited, nbNotVisited, nextNode, indexInNotVisited);

                // Recursive call with updated counts
                permut(visited, nbVisited + 1, notVisited, nbNotVisited - 1, newDistance);

                restoreTables(notVisited, nbNotVisited, nextNode, indexInNotVisited);
            }
        }
    }

    private boolean edgesIntersect(int i, int iNext, int j, int jNext) {
        
        double edge1 = costsMatrix.get(i).get(iNext);
        double edge2 = costsMatrix.get(j).get(jNext);

        double cross1 = costsMatrix.get(i).get(j);
        double cross2 = costsMatrix.get(iNext).get(jNext);

        return edge1 + edge2 > cross1 + cross2;
    }

    private boolean containIntersection(int nbVisited, int lastVisited, int nextNode, int[] visited) {
        for (int j = 0; j < nbVisited - 1; j++) {
            if (edgesIntersect(visited[j], visited[j + 1], lastVisited, nextNode)) {
                return true;
            }
        }
        return false;
    }

    private void updateTables(int[] visited, int nbVisited, int[] notVisited, int nbNotVisited, int nextNode, int i) {
        // Add nextNode to visited
        visited[nbVisited] = nextNode;

        // Swap nextNode with the last unvisited node
        notVisited[i] = notVisited[nbNotVisited - 1];
        notVisited[nbNotVisited - 1] = nextNode;
    }

    private void restoreTables(int[] notVisited, int nbNotVisited, int nextNode, int i) {
        // Swap back to restore original notVisited
        notVisited[nbNotVisited - 1] = notVisited[i];
        notVisited[i] = nextNode;
    }

    private int findIndex(int[] array, int value, int length) {
        for (int idx = 0; idx < length; idx++) {
            if (array[idx] == value) {
                return idx;
            }
        }
        return -1;
    }

    // Getters and setters
    public ArrayList<ArrayList<Double>> getCostsMatrix() {
        return this.costsMatrix;
    }

    public void setCostsMatrix(ArrayList<ArrayList<Double>> costsMatrix) {
        this.costsMatrix = costsMatrix;
    }

    public long getNbCalls() {
        return this.nbCalls;
    }

    public void setNbCalls(long nbCalls) {
        this.nbCalls = nbCalls;
    }

    public BranchAndBound() {
    }
}
