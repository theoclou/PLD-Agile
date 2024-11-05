package com.pld.agile.model.strategy;

import com.pld.agile.model.Solver;
import com.pld.agile.model.entity.Courier;
import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Round;
import com.pld.agile.model.graph.Plan;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        // Création Round
        round.init(2, plan);
        String requestPath = "src/test/java/com/pld/agile/model/strategy/demandeLivraisonTest.xml";
        try {
            round.loadRequests(requestPath);
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }

        List<Integer> vertices = plan.formatInput(round.getDeliveryIntersectionsList());
        System.out.println(vertices);

        Solver solver = new Solver(plan, vertices, new BnBStrategy());
        solver.init();
        solver.solve();
        List<Integer> bestPath = solver.getBestPath();
        List<Intersection> resultIntersection = plan.computeTour(bestPath);
        List<String> result = new ArrayList<>();
        for(Intersection intersection : resultIntersection) {
            result.add(intersection.getId());
        }
        System.out.println("finished");

        assertEquals(Arrays.asList("25611760", "26317233", "26317229", "26057085", "26079655", "26079654", "2835339775", "26079653", "2587460578", "26079653", "2835339775", "26079654", "26057084", "26317233", "25611760", "25611760"), result);

    }

    @Test
    void getBestPath() {
    }
}
