package com.pld.agile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pld.agile.model.Plan;
import com.pld.agile.model.Solver;
import com.pld.agile.model.tsp.TSP;
import com.pld.agile.model.tsp.TSP1;
import com.pld.agile.model.tspOptimized.BranchAndBound;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		// SpringApplication.run(Application.class, args);

		System.out.println("Répertoire courant : " + new File(".").getAbsolutePath());
		// Instanciation de la carte
		Plan plan = new Plan();

		// Lecture du fichier XML
		String filePath = "src/data/petitPlan.xml"; // Remplacez par le chemin réel du fichier XML
		try {
			plan.readXml(filePath);
		} catch (Exception e) {
			System.err.println("Erreur : " + e.getMessage());
			System.exit(1); // Arrêter le programme avec un code d'erreur
		}
		//// testing the TSP method on the whole map
		plan.reIndexIntersections();
		plan.makeCostsMatrix();
		// System.out.println(plan);
		int origin = 1;
		int destination = 48;
		List<Integer> vertices = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
		TSP tsp = new TSP1();
		Solver solver = new Solver(plan, vertices, tsp);
		solver.createCompleteGraph();
		int n = 18;
		long t = System.currentTimeMillis();
		/*
		 * ArrayList<Integer> visited = new ArrayList<>();
		 * visited.add(0);
		 * 
		 * // Create the initial notVisited set with vertices 1 to n-1
		 * Set<Integer> notVisited = new HashSet<>();
		 * for (int i = 1; i < n; i++) {
		 * notVisited.add(i);
		 * }
		 */
		BranchAndBound bnb = new BranchAndBound();
		bnb.setCostsMatrix(solver.getCompleteMatrix());
		int[] visited = new int[n];
		int[] notVisited = new int[n - 1];
		visited[0] = 0;
		for (int i = 0; i < n - 1; i++) {
			notVisited[i] = vertices.get(i + 1);
		}
		bnb.findBestCost();
		System.out.printf("n=%d nbCalls=%d time=%.3fs\n", n, bnb.getNbCalls(),
				(System.currentTimeMillis() - t) / 1000.0);
		solver.solveTSP();

		// Affichage des intersections
		// System.out.println("Intersections:");
		// for (Intersection intersection : plan.getIntersections()) {
		// 	System.out.println(intersection);
		// }

		// // Affichage des sections
		// System.out.println("Sections:");
		// for (Section section : plan.getSections()) {
		// 	System.out.println(section);
		// }

	}
}
