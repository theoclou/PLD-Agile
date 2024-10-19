package com.pld.agile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pld.agile.model.Plan;
import com.pld.agile.model.Solver;
import com.pld.agile.model.tsp.TSP;
import com.pld.agile.model.tsp.TSP1;

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
		List<Integer> vertices = Arrays.asList(1, 48, 49, 45, 2, 38, 92, 158, 17, 15, 33, 16, 5);
		TSP tsp = new TSP1();
		Solver solver = new Solver(plan, vertices, tsp);
		solver.createCompleteGraph();

		solver.solveTSP();

		/*
		 * // Affichage des intersections
		 * System.out.println("Intersections:");
		 * for (Intersection intersection : plan.getIntersections()) {
		 * System.out.println(intersection);
		 * }
		 * 
		 * // Affichage des sections
		 * System.out.println("Sections:");
		 * for (Section section : plan.getSections()) {
		 * System.out.println(section);
		 * }
		 */

	}
}

// Si on part sur ThymeLeaf
/*
 * @Bean
 * public ViewResolver viewResolver() {
 * ClassLoaderTemplateResolver templateResolver = new
 * ClassLoaderTemplateResolver();
 * templateResolver.setPrefix("templates/"); // Dossier des vues
 * templateResolver.setSuffix(".html"); // Extension des fichiers
 * templateResolver.setTemplateMode("HTML");
 * 
 * SpringTemplateEngine engine = new SpringTemplateEngine();
 * engine.setTemplateResolver(templateResolver);
 * 
 * ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
 * viewResolver.setTemplateEngine(engine);
 * return viewResolver;
 * }
 */
