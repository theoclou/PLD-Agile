package com.pld.agile.model.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

/**
 * The {@code KMeansClustering} class provides functionality to perform K-Means clustering
 * on a set of delivery points. It leverages the Apache Commons Math library to cluster
 * delivery points into groups, each potentially assigned to a courier for efficient delivery.
 *
 * <p>
 * This class is particularly useful in scenarios where delivery points need to be
 * distributed among couriers in a way that minimizes travel distance and optimizes delivery routes.
 * </p>
 *
 * @author 
 * @version 1.0
 * @since 2024-04-27
 */
public class KMeansClustering {

    /**
     * Constructs a new {@code KMeansClustering} instance.
     */
    public KMeansClustering() {
    }

    /**
     * Predicts and assigns delivery points to couriers based on their geographical locations
     * using the K-Means clustering algorithm.
     *
     * <p>
     * The method clusters the provided delivery points into a number of groups equivalent to
     * the number of available couriers or the number of delivery points, whichever is smaller.
     * Each group represents the set of delivery points assigned to a specific courier.
     * </p>
     *
     * @param data           a two-dimensional array where each sub-array represents the coordinates
     *                       (e.g., latitude and longitude) of a delivery point.
     * @param couriersNumber the number of couriers available to handle the deliveries.
     * @return an {@code ArrayList} of {@code ArrayList<Integer>} where each inner list contains the
     * indices of delivery points assigned to a specific courier.
     *
     * @throws IllegalArgumentException if {@code couriersNumber} is less than 1.
     */
    public ArrayList<ArrayList<Integer>> predictClusters(double[][] data, int couriersNumber) {
        ArrayList<ArrayList<Integer>> groups = new ArrayList<>();

        // Validate the number of couriers
        if (couriersNumber < 1) {
            throw new IllegalArgumentException("Number of couriers must be at least 1.");
        }

        // If there are no delivery points, return empty groups
        if (data == null || data.length == 0) {
            for (int i = 0; i < couriersNumber; i++) {
                groups.add(new ArrayList<>());
            }
            return groups;
        }

        // Adjust the number of clusters to the minimum between the number of couriers and delivery points
        int effectiveClusters = Math.min(couriersNumber, data.length);
        System.out.println("Number of delivery points: " + data.length);
        System.out.println("Number of effective clusters: " + effectiveClusters);

        // Initialize the K-Means++ clusterer with the effective number of clusters
        KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(effectiveClusters);
        List<CentroidCluster<DoublePoint>> clusters = kMeans.cluster(createPoints(data));

        // Initialize all groups, including those that may remain empty
        for (int i = 0; i < couriersNumber; i++) {
            groups.add(new ArrayList<>());
        }

        System.out.println("\nDistribution of delivery points by courier:");

        // Assign each delivery point to its corresponding cluster
        for (int i = 0; i < data.length; i++) {
            int assignedCluster = getAssignedCluster(data[i], clusters);
            if (assignedCluster >= 0) {
                System.out.printf("Delivery point [%.2f, %.2f] is in Cluster %d%n",
                        data[i][0], data[i][1], assignedCluster);
                groups.get(assignedCluster).add(i);
            }
        }

        return groups;
    }

    /**
     * Converts a two-dimensional array of coordinates into a list of {@code DoublePoint} objects
     * suitable for clustering.
     *
     * @param data a two-dimensional array where each sub-array represents the coordinates
     *             of a delivery point.
     * @return a {@code List} of {@code DoublePoint} instances corresponding to the delivery points.
     */
    private List<DoublePoint> createPoints(double[][] data) {
        return Arrays.stream(data)
                .map(DoublePoint::new)
                .toList();
    }

    /**
     * Determines the cluster index to which a specific delivery point belongs.
     *
     * <p>
     * This method iterates through the clusters and their points to find a match for the given
     * delivery point coordinates.
     * </p>
     *
     * @param point    a one-dimensional array representing the coordinates of a delivery point.
     * @param clusters a list of {@code CentroidCluster<DoublePoint>} representing the clustered groups.
     * @return the index of the cluster to which the delivery point belongs, or {@code -1} if not found.
     */
    private int getAssignedCluster(double[] point, List<CentroidCluster<DoublePoint>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            for (DoublePoint p : clusters.get(i).getPoints()) {
                if (Arrays.equals(p.getPoint(), point)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
