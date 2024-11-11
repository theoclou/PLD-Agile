package com.pld.agile.model.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

public class KMeansClustering {
    public KMeansClustering() {
    }

    public ArrayList<ArrayList<Integer>> predictClusters(double[][] data, int couriersNumber) {
        ArrayList<ArrayList<Integer>> groups = new ArrayList<>();

        // Si pas de points de livraison, retourner des groupes vides
        if (data == null || data.length == 0) {
            for (int i = 0; i < couriersNumber; i++) {
                groups.add(new ArrayList<>());
            }
            return groups;
        }

        // Ajuster le nombre de clusters au minimum entre le nombre de livreurs et de points
        int effectiveClusters = Math.min(couriersNumber, data.length);
        System.out.println("Number of delivery points: " + data.length);
        System.out.println("Number of effective clusters: " + effectiveClusters);

        // Créer le clusterer avec le nombre effectif de clusters
        KMeansPlusPlusClusterer<DoublePoint> kMeans = new KMeansPlusPlusClusterer<>(effectiveClusters);
        List<CentroidCluster<DoublePoint>> clusters = kMeans.cluster(createPoints(data));

        // Initialiser tous les groupes (même ceux qui seront vides)
        for (int i = 0; i < couriersNumber; i++) {
            groups.add(new ArrayList<>());
        }

        System.out.println("\nDistribution of delivery points by courier:");
        // Assigner les points aux clusters effectifs
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

    private List<DoublePoint> createPoints(double[][] data) {
        return Arrays.stream(data)
                .map(DoublePoint::new)
                .toList();
    }

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