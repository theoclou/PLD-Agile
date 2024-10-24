package com.pld.agile.model.strategy;

import com.pld.agile.model.Solver;
import com.pld.agile.model.entity.Courier;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.graph.Plan;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TSPTest {

    @Test
    void TSPTest() {

        Plan plan = new Plan();
        Round round = new Round();

        String filePath = "src/test/java/com/pld/agile/model/strategy/petitPlanTest.xml";
        try {
            plan.readXml(filePath);
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            System.exit(1); // Arrêter le programme avec un code d'erreur
        }
        plan.preprocessData();
        System.out.println(plan);
        // Création Round
        round.init(2, plan);
        String requestPath = "src/test/java/com/pld/agile/model/strategy/demandeLivraisonTest.xml";
        try {
            round.loadRequests(requestPath);
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }

        System.out.println(round.getDeliveryIntersectionsList());
        List<Integer> vertices = plan.formatInput(round.getDeliveryIntersectionsList());
        System.out.println(vertices);

        Solver solver = new Solver(plan, vertices, new BnBStrategy());
        solver.init();
        solver.solve();
        List<Integer> bestPath = solver.getBestPath();
        System.out.println(bestPath);
        plan.computeTour(bestPath);
        System.out.println("finished");

        //Actuellement le résultat est pas le bon

    }

    @Test
    void getBestPath() {
    }
}
