package com.pld.agile.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pld.agile.model.graph.CompleteGraph;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.SolvingStrategy;

/**
 * The {@code Solver} class is responsible for solving a Traveling Salesman
 * Problem (TSP)
 * based on a plan of intersections and sections. It utilizes a strategy pattern
 * for
 * solving the problem and works with a complete graph representation.
 */
public class Solver {
    private List<Integer> vertices = new ArrayList<>();
    private ArrayList<ArrayList<Double>> completeMatrix = new ArrayList<>();
    private Plan plan;
    private SolvingStrategy solvingStrategy;
    private CompleteGraph g;
    private Map<String, Object> resultPoint;
    private List <Integer> bestPath=new ArrayList<>();
    /**
     * Constructs a {@code Solver} with the given plan, vertices, and solving
     * strategy.
     *
     * @param plan            the {@code Plan} object representing intersections and
     *                        sections
     * @param vertices        the list of vertices to include in the TSP
     * @param solvingStrategy the strategy used to solve the TSP
     */
    public Solver(Plan plan, List<Integer> vertices, SolvingStrategy solvingStrategy) {
        this.plan = plan;
        this.vertices = vertices;
        this.solvingStrategy = solvingStrategy;
        this.resultPoint = new HashMap<>();
    }

    /**
     * Initializes the solver by creating a complete graph using the given plan and
     * vertices.
     *
     * @return the {@code Solver} object after initialization
     */
    public Solver init() {
        this.createCompleteGraph();
        return this;
    }

    /**
     * Fills the {@code completeMatrix} with distances between vertices to create
     * a complete graph representation.
     */
    public CompleteGraph createCompleteGraph() {
        int size = vertices.size();
        for (int i = 0; i < size; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    row.add(-1.0); // Distance to self is set to -1
                } else {
                    Double distance = plan.findShortestDistance(vertices.get(i), vertices.get(j));
                    row.add(distance); // Add distance between vertices
                }
            }
            completeMatrix.add(row);
        }
        g = new CompleteGraph(completeMatrix.size(), completeMatrix);
        return g;
    }

    /**
     * Solves the TSP using the provided solving strategy.
     */
    public void solve() {
        solvingStrategy.solve(g);
    }

    /**
     * Returns the list of vertices involved in the TSP.
     *
     * @return the list of vertices
     */
    public List<Integer> getVertices() {
        return this.vertices;
    }

    /**
     * Sets the list of vertices for the TSP.
     *
     * @param vertices the list of vertices to set
     */
    public void setVertices(List<Integer> vertices) {
        this.vertices = vertices;
    }

    /**
     * Returns the complete distance matrix.
     *
     * @return the complete matrix representing distances between vertices
     */
    public ArrayList<ArrayList<Double>> getCompleteMatrix() {
        return this.completeMatrix;
    }

    /**
     * Sets the complete matrix representing distances between vertices.
     *
     * @param completeMatrix the complete matrix to set
     */
    public void setCompleteMatrix(ArrayList<ArrayList<Double>> completeMatrix) {
        this.completeMatrix = completeMatrix;
    }

    /**
     * Returns the plan used in this solver.
     *
     * @return the plan
     */
    public Plan getPlan() {
        return this.plan;
    }

    /**
     * Sets the plan for this solver.
     *
     * @param plan the plan to set
     */
    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    /**
     * Returns the best path found by the solving strategy.
     *
     * @return the best path as a list of vertices
     */
    public List<Integer> getBestPath() {
        List<Integer> path = solvingStrategy.getBestPath();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            result.add(vertices.get(path.get(i)));
        }
        this.bestPath=result;
        return result;
    }

    /**
     * Returns the completeGraph object used to solve the tsp
     *
     * @return the complete graph
     */
    public CompleteGraph getCompleteGraph() {
        return this.g;
    }

    /**
     * Returns the cost of the best path found by the solving strategy.
     *
     * @return the best cost
     */
    public double getBestCost() {
        return solvingStrategy.getBestCost();
    }

    public List<Integer> addDeliveryPoint(Integer intersection, List<Integer> bestPath) {
        vertices.add(intersection);
        g = createCompleteGraph();
        Double minimuDistance = Double.MAX_VALUE;
        Integer index = 0;
        for (int i = 0; i < bestPath.size() - 1; i++) {
            Double currentDistance = g.getCost(bestPath.get(i), intersection)
                    + g.getCost(intersection, bestPath.get(i));
            if (currentDistance < minimuDistance) {
                minimuDistance = currentDistance;
                index = i;
            }
        }
        bestPath.add(index + 1, intersection);
        return bestPath;
    }

    public List<Integer> deleteDeliveryPoint(Integer intersection, List<Integer> bestPath) {
        if (!bestPath.contains(intersection)) { // Check if intersection is in bestPath
            throw new IllegalArgumentException("Error: Intersection not in bestPath");
        } else {
            bestPath.remove(intersection); // Remove intersection if it exists in bestPath
        }
        return bestPath; // Return the modified bestPath
    }

    /**
     * Returns the best possible path that can be served within the time limit.
     *
     * @return the best possible path as a list of vertices
     */
    public List<Integer> getBestPossiblePath() {
        if (this.bestPath.size()==0)
        {
            this.bestPath = getBestPath();
        }
        int servedPoints = (int) resultPoint.get("served");
        System.out.println("We will serve :" + servedPoints + " points");
        List<Integer> bestPathSubList = bestPath.subList(0, servedPoints + 1);
        if (servedPoints > 0 && bestPathSubList.getFirst() != bestPathSubList.getLast()) {
            bestPathSubList.add(bestPathSubList.getFirst());
        }
        return bestPathSubList;
    }

    public Map<Integer, LocalTime> getPointsWithTime() {
        return (Map<Integer, LocalTime>) resultPoint.get("pointsWithTime");
    }

    /**
     * Returns the best possible cost of serving the points within the time limit.
     *
     * @return the best possible cost
     */
    public double getBestPossibleCost() {

        double cost = (double) resultPoint.get("cost");
        return cost;
    }

    public void computePointsToBeServed() {
        pointsToBeServed();
    }

    /**
     * Determines how many points can be served and the cost within a given time
     * limit (8 hours), given that the courier is traveling at 15 km/h and spends
     * an additional 5 minutes at each delivery point.
     */
    // private void pointsToBeServed() {
    // List<Integer> bestPath = getBestPath();
    // Map<Integer, LocalTime> pointsWithTime = new HashMap<>();
    // double currentCost = 0.0;
    // double cumulativeTime = 0.0; // in hours
    // int servedPoints = 0;
    // double speed = 0.001; // km/h
    // double serviceTimePerPoint = 5.0 / 60.0; // in hours (5 minutes)
    // double timeLimit = 8.0; // in hours
    // LocalTime currentTime = LocalTime.of(8, 0);
    // int pathSize = bestPath.size();
    // int initialPosition = bestPath.get(0);

    // for (int i = 0; i < pathSize - 1; i++) {
    // int currentPosition = bestPath.get(i);
    // int nextPosition = bestPath.get(i + 1);

    // // Compute distance and time to the next point
    // double distanceToNextMeters = g.getCost(currentPosition, nextPosition); // in
    // meters
    // double distanceKmToNext = distanceToNextMeters / 1000.0; // convert to
    // kilometers
    // double timeToNextPoint = distanceKmToNext / speed; // time in hours

    // // Service time at the next point
    // double serviceTimeAtNextPoint = serviceTimePerPoint;

    // // Compute time to return home from the next point
    // double distanceToHomeFromNextMeters = g.getCost(nextPosition,
    // initialPosition);
    // double timeToReturnHomeFromNext = (distanceToHomeFromNextMeters / 1000.0) /
    // speed; // time in hours

    // // Compute total time if proceeding to the next point and then returning home
    // double totalTimeIfProceedAndReturn = cumulativeTime + timeToNextPoint +
    // serviceTimeAtNextPoint + timeToReturnHomeFromNext;

    // // Check if total time exceeds the time limit
    // if (totalTimeIfProceedAndReturn > timeLimit) {
    // // Return home from the current position
    // double distanceToHomeFromCurrentMeters = g.getCost(currentPosition,
    // initialPosition);
    // double timeToReturnHomeFromCurrent = (distanceToHomeFromCurrentMeters /
    // 1000.0) / speed;

    // cumulativeTime += timeToReturnHomeFromCurrent;
    // // Update current time
    // currentTime = LocalTime.of(8, 0).plusSeconds((long) (cumulativeTime * 3600));

    // // Add cost to return home
    // currentCost += distanceToHomeFromCurrentMeters;

    // // Break the loop
    // break;
    // } else {
    // // Proceed to the next point
    // cumulativeTime += timeToNextPoint + serviceTimeAtNextPoint;

    // // Update current time
    // currentTime = LocalTime.of(8, 0).plusSeconds((long) (cumulativeTime * 3600));

    // currentCost += distanceToNextMeters;

    // servedPoints = i + 1;

    // pointsWithTime.put(currentPosition, currentTime);
    // }
    // }

    // // After the loop, return home from the last visited position if within time
    // limit
    // if (cumulativeTime <= timeLimit) {
    // int lastPosition = servedPoints > 0 ? bestPath.get(servedPoints) :
    // initialPosition;

    // double distanceToHomeFromLastMeters = g.getCost(lastPosition,
    // initialPosition);
    // double timeToReturnHomeFromLast = (distanceToHomeFromLastMeters / 1000.0) /
    // speed;

    // cumulativeTime += timeToReturnHomeFromLast;
    // currentTime = LocalTime.of(8, 0).plusSeconds((long) (cumulativeTime * 3600));

    // currentCost += distanceToHomeFromLastMeters;

    // pointsWithTime.put(initialPosition, currentTime);
    // }

    // resultPoint.put("served", servedPoints);
    // resultPoint.put("cost", currentCost);
    // resultPoint.put("pointsWithTime", pointsWithTime);
    // }
    private void pointsToBeServed() {
        if (this.bestPath.size()==0)
        {
            this.bestPath = getBestPath();
        }

        Map<Integer, LocalTime> pointsWithTime = new HashMap<>();
        double currentCost = 0.0;
        double cumulativeTime = 0.0; // in hours
        int servedPoints = 0;
        double speed = 1.0; // km/h
        double serviceTimePerPoint = 5.0 / 60.0; // in hours (5 minutes)
        double timeLimit = 8.0; // in hours
        LocalTime currentTime = LocalTime.of(8, 0);
        int pathSize = this.bestPath.size();
        for (int i = 0; i < pathSize - 1; i++) {
            int currentPosition = this.bestPath.get(i);
            int nextPosition = this.bestPath.get(i + 1);
            double distanceMeters = g.getCost(i, (i + 1) % vertices.size()); // in meters

            double distanceKm = distanceMeters / 1000.0; // convert to kilometers
            double timeToNextPoint = distanceKm / speed; // time in hours
            cumulativeTime += timeToNextPoint;

            // Add service time at each delivery point (except for the starting point)
            if (i > 0) {
                cumulativeTime += serviceTimePerPoint;
            }

            // Check if the cumulative time exceeds the 8-hour limit
            if (cumulativeTime > timeLimit) {
                break;
            }

            currentCost += distanceMeters;
            servedPoints = i + 1; // since i starts from 0
            pointsWithTime.put(currentPosition, currentTime);
            // Update current time
            currentTime = LocalTime.of(8, 0).plusSeconds((long) (cumulativeTime * 3600));

        }
        pointsWithTime.put(this.bestPath.getLast(), currentTime);

        resultPoint.put("served", servedPoints);
        resultPoint.put("cost", currentCost);
        resultPoint.put("pointsWithTime", pointsWithTime);
    }

}
