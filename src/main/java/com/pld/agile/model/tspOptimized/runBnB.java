package com.pld.agile.model.tspOptimized;


import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.pld.agile.model.BnBStrategy;
import com.pld.agile.model.Plan;
import com.pld.agile.model.Solver;
import com.pld.agile.model.TspStrategy;
import com.pld.agile.model.tsp.TSP;
import com.pld.agile.model.tsp.TSP1;

public class runBnB {
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

		List<Integer> vertices = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8,9, 10 ,11);
		Solver solver = new Solver(plan, vertices, new TspStrategy()).init();
		int n = 12;
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

		//v1
		solver.solve();
		//v2
		Solver solverbnb = new Solver(plan, vertices, new BnBStrategy()).init();
		solverbnb.solve();
		System.out.printf("n=%d time=%.3fs\n", n,
				(System.currentTimeMillis() - t) / 1000.0);


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
