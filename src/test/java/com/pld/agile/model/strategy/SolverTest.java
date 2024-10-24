package com.pld.agile.model.strategy;

import com.pld.agile.model.Solver;
import com.pld.agile.model.graph.Plan;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class TSPTest {

    @Test
    void TSPTest() {

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
        Solver solver = new Solver(plan, vertices, new BnBStrategy());
        solver.init();
        solver.solve();
        List<Integer> bestPath = solver.getBestPath();
        System.out.println(bestPath);
        plan.computeTour(bestPath);
        System.out.println("finished");
    }

    @Test
    void getBestPath() {
    }
}