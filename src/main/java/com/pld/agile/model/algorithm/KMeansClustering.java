package com.pld.agile.model.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

public class KMeansClustering {
    public KMeansClustering(){

    }
    
    public  ArrayList<ArrayList<Integer>> predictClusters(double[][] data, int couriersNumber) {
        // Create a KMeans++ clusterer
        ArrayList<ArrayList<Integer>> groups= new ArrayList<>();
        System.out.println("Number of couriers: " + couriersNumber);
        KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(couriersNumber);
        List<CentroidCluster<DoublePoint>> clusters = kMeans.cluster(createPoints(data));

        // Print cluster centers and labels
        System.out.println("Cluster Centers:");
        int clusterNumber = 0;
        for (CentroidCluster<DoublePoint> cluster : clusters) {
            DoublePoint centroid = (DoublePoint) cluster.getCenter();
            System.out.printf("Cluster %d center: [%.2f, %.2f]%n", clusterNumber, centroid.getPoint()[0],
                    centroid.getPoint()[1]);
            clusterNumber++;
            groups.add(new ArrayList<>());
        }

        System.out.println("\n Distribution of delivery points by courier:");
        for (int i = 0; i < data.length; i++) {
            int assignedCluster = getAssignedCluster(data[i], clusters);
            System.out.printf("Delivery point [%.2f, %.2f] is in Cluster %d%n", data[i][0], data[i][1], assignedCluster);
            groups.get(assignedCluster).add(i);
        }
        return groups;
    }

    private  List<DoublePoint> createPoints(double[][] data) {
        return Arrays.stream(data)
                .map(DoublePoint::new)
                .toList();
    }

    private  int getAssignedCluster(double[] point, List<CentroidCluster<DoublePoint>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            for (DoublePoint p : clusters.get(i).getPoints()) {
                if (Arrays.equals(p.getPoint(), point)) {
                    return i;
                }
            }
        }
        return -1; // If not found, return an invalid index
    }
}

