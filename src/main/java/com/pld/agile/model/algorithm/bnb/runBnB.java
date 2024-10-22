package com.pld.agile.model.algorithm.bnb;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.pld.agile.model.Solver;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.BnBStrategy;
import com.pld.agile.model.strategy.TspStrategy;

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

		List<Integer> vertices = Arrays.asList(0, 1, 2, 3, 4, 5);
		Solver solver = new Solver(plan, vertices, new TspStrategy()).init();
		int n = 6;
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

		// v1
		long t1 = System.currentTimeMillis();
		solver.solve();
		System.out.printf("n=%d time=%.3fs\n", n,
				(System.currentTimeMillis() - t1) / 1000.0);
		// v2
		long t2 = System.currentTimeMillis();

		Solver solverbnb = new Solver(plan, vertices, new BnBStrategy());
		solverbnb.createCompleteGraph();
		solverbnb.solve();
		System.out.printf("n=%d time=%.3fs\n", n,
				(System.currentTimeMillis() - t2) / 1000.0);

	}
}
