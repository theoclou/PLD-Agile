package com.pld.agile;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pld.agile.model.Solver;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.BnBStrategy;

@SpringBootApplication
public class Application {
	// TODO
	// Faire test en JUnit
	public static void main(String[] args) {

		// Launch App
		 SpringApplication.run(Application.class, args);

		Plan plan = new Plan();

		String filePath = "src/data/grandPlan.xml";
		try {
			plan.readXml(filePath);
		} catch (Exception e) {
			System.err.println("Erreur : " + e.getMessage());
			System.exit(1); // Arrêter le programme avec un code d'erreur
		}

		plan.preprocessData();
		List<Integer> vertices = Arrays.asList(0, 256, 233, 127);
		/*
		 * If using a list of ids use instead this :
		 * List<Integer> vertices =plan.formatInput(List<String> idIntersections)
		 */

		Solver solver = new Solver(plan, vertices, new BnBStrategy());
		solver.init();
		solver.solve();
		solver.computePointsToBeServed();
		List<Integer> bestPath = solver.getBestPossiblePath();
		System.out.println(bestPath);
		plan.computeTour(bestPath);

		// // Création Round
		// String requestPath = "src/data/demandeGrand9.xml";
		// try {
		// 	round.loadRequests(requestPath);
		// } catch (Exception e) {
		// 	System.err.println("Erreur : " + e.getMessage());
		// }
		// ArrayList<ArrayList<String>> groups = round.computeRoundOptimized();
		// System.out.println(groups);
		// System.exit(0);
	}
}
