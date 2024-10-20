package com.pld.agile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;


import com.pld.agile.model.Courier;
import com.pld.agile.model.Plan;
import com.pld.agile.model.Round;
import com.pld.agile.model.Solver;
import com.pld.agile.model.tsp.TSP;
import com.pld.agile.model.tsp.TSP1;
import com.pld.agile.model.tspOptimized.BranchAndBound;

@SpringBootApplication
public class Application {
	//TODO
	//Faire test en JUnit
	public static void main(String[] args) {

		// Launch App
		SpringApplication.run(Application.class, args);

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
		List<Integer> vertices = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8,9);
		TSP tsp = new TSP1();
		Solver solver = new Solver(plan, vertices, tsp);
		solver.createCompleteGraph();
		int n = 10;
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

		// Création Round
		Round round = new Round();
		List<Courier> couriers = new ArrayList<>();
		round.init(couriers, plan);
		String requestPath = "src/data/demandePetit2.xml";
		try{
			round.loadRequests(requestPath);
		}catch (Exception e){
			System.err.println("Erreur : " + e.getMessage());
		}

//		// Affichage des intersections
//		System.out.println("Intersections:");
//		for (Intersection intersection : plan.getIntersections()) {
//			System.out.println(intersection);
//		}
//
//		// Affichage des sections
//		System.out.println("Sections:");
//		for (Section section : plan.getSections()) {
//			System.out.println(section);
//		}
	}
}
