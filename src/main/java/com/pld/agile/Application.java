package com.pld.agile;

import com.pld.agile.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {
	//TODO
	//Faire test en JUnit
	public static void main(String[] args) {

//		SpringApplication.run(Application.class, args);

		System.out.println("Répertoire courant : " + new File(".").getAbsolutePath());
		// Instanciation de la carte
		Plan plan = new Plan();

		// Lecture du fichier XML
		String filePath = "src/data/petitPlan.xml"; // Remplacez par le chemin réel du fichier XML
		try {
			plan.readXml(filePath);
		} catch (Exception e) {
			System.err.println("Erreur : " + e.getMessage());
			System.exit(1);  // Arrêter le programme avec un code d'erreur
		}

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

	//Si on part sur ThymeLeaf
	/*@Bean
	public ViewResolver viewResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templates/");  // Dossier des vues
		templateResolver.setSuffix(".html");       // Extension des fichiers
		templateResolver.setTemplateMode("HTML");

		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver);

		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(engine);
		return viewResolver;
	}*/


