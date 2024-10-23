package com.pld.agile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pld.agile.model.*;
import com.pld.agile.model.entity.Courier;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.TspStrategy;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;



import com.pld.agile.model.algorithm.bnb.BranchAndBound;

@SpringBootApplication
public class Application {
	//TODO
	//Faire test en JUnit
	public static void main(String[] args) {

		// Launch App
		SpringApplication.run(Application.class, args);

		Plan plan = new Plan();

		String filePath = "src/data/petitPlan.xml";
		try {
			plan.readXml(filePath);
		} catch (Exception e) {
			System.err.println("Erreur : " + e.getMessage());
			System.exit(1); // Arrêter le programme avec un code d'erreur
		}
		
		plan.preprocessData();
		List<Integer> vertices = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8,9);
		Solver solver = new Solver(plan, vertices, new TspStrategy());
		solver.createCompleteGraph();
		int n = 10;
		long t = System.currentTimeMillis();
		
		BranchAndBound bnb = new BranchAndBound();
		bnb.setCostsMatrix(solver.getCompleteMatrix());
		
		bnb.findBestCost();
		System.out.printf("n=%d nbCalls=%d time=%.3fs\n", n, bnb.getNbCalls(),
				(System.currentTimeMillis() - t) / 1000.0);
		solver.solve();
		List<Integer> bestPath=solver.getBestPath();
		plan.computeTour(bestPath);
		System.out.println("finished here");


		// Création Round
		Round round = new Round();
		List<Courier> couriers = new ArrayList<>();
		round.init(2, plan);
		String requestPath = "src/data/demandePetit2.xml";
		try{
			round.loadRequests(requestPath);
		}catch (Exception e){
			System.err.println("Erreur : " + e.getMessage());
		}
	}
}
