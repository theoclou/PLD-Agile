package com.pld.agile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.pld.agile.model.User; // Correct import for User

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		// Create a User object properly inside the main method
		User user = new User(0, "abderrahmane", "abderrahmane@gmail.com", "06864", "password");
		System.out.println("User created: " + user);
	}

	// If using Thymeleaf, uncomment this section
    /*
    @Bean
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
    }
    */
}
