package com.pld.agile.model.algorithm.bnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.pld.agile.model.graph.CompleteGraph;

/**
 * The {@code BranchAndBound} class implements a branch and bound algorithm to
 * solve
 * the Traveling Salesman Problem (TSP). It finds the shortest path visiting all
 * nodes
 * in a given cost matrix and returns to the starting node, minimizing the total
 * distance.
 */
public class BranchAndBound {
    private long nbCalls = 0; // Number of calls to the recursive permut function
    // private ArrayList<ArrayList<Double>> costsMatrix = new ArrayList<>();
    private double best = Double.MAX_VALUE;
    private int[] bestPath; // To store the best path found
    private CompleteGraph g;

    /**
     * Constructor for the BranchAndBound class.
     */
    public BranchAndBound(CompleteGraph g) {
        this.g = g;
    }

    /**
     * Constructor for the BranchAndBound class.
     */
    public BranchAndBound() {
    }

    /**
     * Starts the process of finding the best cost and path using the branch and
     * bound
     * algorithm. This method initiates the permutation process, aiming to find the
     * shortest
     * path visiting all nodes and returning to the starting point.
     */
    public void findBestCost() {
        int n = g.getNbVertices(); // Number of nodes
        int[] visited = new int[n + 1]; // +1 to include the return to the starting point
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
        System.out.println("Optimized solution :");
        System.out.println("Best cost: " + best);
        System.out.print("Best path: ");
        for (int i = 0; i <= n; i++) {
            System.out.print(bestPath[i] + " ");
        }
        System.out.println();
    }

    /**
     * Recursive method that generates permutations of the visited nodes and
     * calculates
     * the distance traveled. It eliminates paths that exceed the current best
     * distance
     * and checks for intersection to ensure no crossing edges.It also reorders the
     * ramaining nodes to visit the nearest ones first.
     *
     * @param visited      the list of already visited nodes
     * @param nbVisited    the number of visited nodes
     * @param notVisited   the list of unvisited nodes
     * @param nbNotVisited the number of unvisited nodes
     * @param distance     the current total distance traveled
     */
    private void permut(int[] visited, int nbVisited, int[] notVisited, int nbNotVisited, double distance) {
        nbCalls++;

        // eliminate paths exceeding current best distance
        if (distance >= best) {
            return;
        }

        // Base case: all nodes have been visited
        if (nbNotVisited == 0) {
            // Add distance to return to the starting point
            distance += g.getCost(visited[nbVisited - 1], 0);
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
        Arrays.sort(sortedNotVisited, Comparator.comparingDouble(a -> g.getCost(lastVisited, a)));

        // Iterate over sorted nodes
        for (int idx = 0; idx < nbNotVisited; idx++) {
            int nextNode = sortedNotVisited[idx];
            double newDistance = distance + g.getCost(lastVisited, nextNode);

            // Eliminate paths exceeding current best distance
            if (newDistance >= best) {
                continue;
            }

            // intersection check
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

    /**
     * Checks if the edges formed by two pairs of nodes intersect, i.e., whether
     * they cross
     * each other in the path. This ensures that paths remain non-intersecting.
     *
     * @param i     the first node of the first edge
     * @param iNext the second node of the first edge
     * @param j     the first node of the second edge
     * @param jNext the second node of the second edge
     * @return {@code true} if the edges intersect, {@code false} otherwise
     */
    private boolean edgesIntersect(int i, int iNext, int j, int jNext) {
        double edge1 = g.getCost(i, iNext);
        double edge2 = g.getCost(j, jNext);
        double cross1 = g.getCost(i, j);
        double cross2 = g.getCost(iNext, jNext);
        return edge1 + edge2 > cross1 + cross2;
    }

    /**
     * Checks if adding the next node will create any intersections with previously
     * visited edges. It compares the new edge formed with all previously visited
     * edges.
     *
     * @param nbVisited   the number of nodes visited so far
     * @param lastVisited the last node visited
     * @param nextNode    the next node to visit
     * @param visited     the array of visited nodes
     * @return {@code true} if an intersection is found, {@code false} otherwise
     */
    private boolean containIntersection(int nbVisited, int lastVisited, int nextNode, int[] visited) {
        for (int j = 0; j < nbVisited - 1; j++) {
            if (edgesIntersect(visited[j], visited[j + 1], lastVisited, nextNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the visited and notVisited arrays by marking the next node as visited
     * and swapping it with the last unvisited node in the notVisited array.
     *
     * @param visited      the array of visited nodes
     * @param nbVisited    the number of visited nodes
     * @param notVisited   the array of notVisited nodes
     * @param nbNotVisited the number of unvisited nodes
     * @param nextNode     the next node to visit
     * @param i            the index of nextNode in notVisited array
     */
    private void updateTables(int[] visited, int nbVisited, int[] notVisited, int nbNotVisited, int nextNode, int i) {
        visited[nbVisited] = nextNode;
        notVisited[i] = notVisited[nbNotVisited - 1];
        notVisited[nbNotVisited - 1] = nextNode;
    }

    /**
     * Restores the notVisited array by swapping the last unvisited node back to its
     * original position after the recursive call.
     *
     * @param notVisited   the array of unvisited nodes
     * @param nbNotVisited the number of unvisited nodes
     * @param nextNode     the next node that was visited
     * @param i            the index of nextNode in notVisited array
     */
    private void restoreTables(int[] notVisited, int nbNotVisited, int nextNode, int i) {
        notVisited[nbNotVisited - 1] = notVisited[i];
        notVisited[i] = nextNode;
    }

    /**
     * Finds the index of a specific value in an array up to the given length.
     *
     * @param array  the array to search
     * @param value  the value to find
     * @param length the length of the array to consider
     * @return the index of the value in the array, or -1 if not found
     */
    private int findIndex(int[] array, int value, int length) {
        for (int idx = 0; idx < length; idx++) {
            if (array[idx] == value) {
                return idx;
            }
        }
        return -1;
    }

    /**
     * Returns the cost matrix used by the algorithm.
     *
     * @return the cost matrix
     */
    public ArrayList<ArrayList<Double>> getCostsMatrix() {
        return getCostsMatrix();
    }

    /**
     * Sets the cost matrix for the algorithm.
     *
     * @param costsMatrix the cost matrix to set
     */
    // public void setCostsMatrix(ArrayList<ArrayList<Double>> costsMatrix) {
    // this.costsMatrix = costsMatrix;
    // }

    /**
     * Returns the number of recursive calls made during the execution.
     *
     * @return the number of calls
     */
    public long getNbCalls() {
        return this.nbCalls;
    }

    /**
     * Sets the number of recursive calls made during the execution.
     *
     * @param nbCalls the number of calls to set
     */
    public void setNbCalls(long nbCalls) {
        this.nbCalls = nbCalls;
    }

    /**
     * Sets the number of recursive calls made during the execution.
     *
     * @param completeGraph the number of calls to set
     */
    public void setCompleteGraph(CompleteGraph completeGraph) {
        this.g = completeGraph;
    }

    /**
     * Returns the best path found during the execution as a {@code List<Integer>}.
     *
     * @return the best path
     */
    public List<Integer> getBestPath() {
        List<Integer> integerList = Arrays.stream(this.bestPath) // Convert int[] to IntStream
                .boxed() // Convert IntStream to Stream<Integer>
                .collect(Collectors.toList());
        return integerList;
    }
}
