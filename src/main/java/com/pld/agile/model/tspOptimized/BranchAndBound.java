package com.pld.agile.model.tspOptimized;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.pld.agile.model.Plan;

public class BranchAndBound {
    private long nbCalls = 0; // Number of calls to the recursive permut function
    private Plan plan;
    private ArrayList<ArrayList<Double>> costsMatrix = new ArrayList<>();
    private Double best = Double.MAX_VALUE;
    



    public void finBestCost(int[] visited, int[] notVisited) {
        double distance = 0.0;
        visited[0] = 0; // Start from node 0
        permut(visited, 1, notVisited, notVisited.length, distance);
        System.out.println(best);
    }





    private void printTable(ArrayList<Integer> visited) {
        for (int vertex : visited) {
            System.out.print(vertex + " ");
        }
        System.out.println("0");
    }




    private boolean edgesIntersect(int i, int iNext, int j, int jNext) {
        // cost of (v_i,v_i+1)
        double edge1 = costsMatrix.get(i).get(iNext);
        // cost of (v_j,v_j+1)
        double edge2 = costsMatrix.get(j).get(jNext);

        // cost of (v_i, v_j)
        double cross1 = costsMatrix.get(i).get(j);

        // cost of (v_i+1, v_j+1)
        double cross2 = costsMatrix.get(iNext).get(jNext);

        return edge1 + edge2 > cross1 + cross2;
    }




    private boolean containIntersection(int nbVisited, int lastVisited, int nextNode, int[] visited) {
        for (int j = 0; j < nbVisited - 1; j++) {
            if (edgesIntersect(lastVisited, nextNode, visited[j], visited[j + 1])) {
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




    private void restoreTables(int [] notVisited,int nbNotVisited,int nextNode,int i)
    {
        // Swap back to restore original notVisited
        notVisited[nbNotVisited - 1] = notVisited[i];
        notVisited[i] = nextNode;
    }



    private double calculateLowerBound(int[] notVisited, int nbNotVisited) {
        double minEdge = Double.MAX_VALUE;
        for (int i = 0; i < nbNotVisited; i++) {
            int node = notVisited[i];
            for (int j = 0; j < costsMatrix.size(); j++) {
                if (minEdge >= costsMatrix.get(node).get(j) && node != j) {
                    minEdge = costsMatrix.get(node).get(j);
                }
            }
        }
        return minEdge;
    }
    




    private void permut(int[] visited, int nbVisited, int[] notVisited, int nbNotVisited, double distance) {
        nbCalls++;
        if (distance >= best) {
            return;
        }
        // Base case: all nodes have been visited
        if (nbNotVisited == 0) {
            // Add distance to return to the starting point
            distance += costsMatrix.get(visited[nbVisited - 1]).get(0);
            if (distance < best) {
                best = distance;
            }
            return;
        }
        for (int i = 0; i < nbNotVisited; i++) {
            int nextNode = notVisited[i];
            int lastVisited = visited[nbVisited - 1];
            double newDistance = distance + costsMatrix.get(lastVisited).get(nextNode);
            if (newDistance >= best) {
                continue;
            }
            // Edge intersection check
            boolean hasIntersection = containIntersection(nbVisited, lastVisited, nextNode, visited);
            if (!hasIntersection) {
                updateTables(visited, nbVisited, notVisited, nbNotVisited, nextNode, i);
                // Recursive call with updated counts
                permut(visited, nbVisited + 1, notVisited, nbNotVisited - 1, newDistance);
                restoreTables( notVisited, nbNotVisited, nextNode, i);
            }
        }
    }






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
