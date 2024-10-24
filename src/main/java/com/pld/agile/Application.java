package com.pld.agile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	// TODO
	// Faire test en JUnit
	public static void main(String[] args) {

		// Launch App
		SpringApplication.run(Application.class, args);

		/*
		Plan plan = new Plan();

		String filePath = "src/data/petitPlan.xml";
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
		/*
		Solver solver = new Solver(plan, vertices, new BnBStrategy());
		solver.init();
		solver.solve();
		List<Integer> bestPath = solver.getBestPath();
		System.out.println(bestPath);
		plan.computeTour(bestPath);
		System.out.println("finished");


		// Création Round
		Round round = new Round();
		List<Courier> couriers = new ArrayList<>();
		round.init(2, plan);
		String requestPath = "src/data/demandePetit2.xml";
		try {
		round.loadRequests(requestPath);
		} catch (Exception e) {
		System.err.println("Erreur : " + e.getMessage());
		}
		System.exit(0);*/
	}
}
