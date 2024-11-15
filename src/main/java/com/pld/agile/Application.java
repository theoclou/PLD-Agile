package com.pld.agile;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pld.agile.model.Solver;
import com.pld.agile.model.entity.Courier;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.graph.Plan;
import com.pld.agile.model.strategy.BnBStrategy;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		// Launch App
		 SpringApplication.run(Application.class, args);
	}
}
