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
            System.err.println("Error : " + e.getMessage());
            System.exit(1);
        }
        plan.preprocessData();
        // Création Round
        round.init(1, plan);
        String requestPath = "src/test/java/com/pld/agile/model/strategy/demandeLivraisonTest.xml";
        try {
            round.loadRequests(requestPath);
        } catch (Exception e) {
            System.err.println("Error : " + e.getMessage());
            System.exit(1);
        }

        List<Integer> vertices = plan.formatInput(round.getDeliveryIntersectionsList());

        Solver solver = new Solver(plan, vertices, new BnBStrategy());
		solver.init();
		solver.solve();
		solver.computePointsToBeServed();
        List<Integer> bestPath = solver.getBestPath();
        List<Intersection> resultIntersection = plan.computeTour(bestPath);

        List<String> result = new ArrayList<>();
        for(Intersection intersection : resultIntersection) {
            result.add(intersection.getId());
        }
        System.out.println("finished");

        assertEquals(
                Arrays.asList("208769457, 342868447, 208769312, 208769295, 208769334, 48830474, 48830471, 48830470, 1368674811, 1368674810, 55463805, 26317234, 26317228, 26317232, 26317229, 26317233, 25611760, 250042857, 25336179, 250042857, 25611760, 26317233, 26317229, 26317232, 26317228, 26317234, 55463805, 1368674809, 1368674810, 1368674811, 48830470, 48830471, 48830474, 208769334, 208769295, 208769312, 342868447, 208769457, 208769457").toString(),
                result.toString()
        );


        // Testing the add/delete delivery points after "Compute"
        ArrayList<ArrayList<String>> groups = round.computeRoundOptimized();
		String intersectionId ="208769039";
        Integer intersectionIndex = plan.getIndexById(intersectionId);

        List<Integer> bestPathUpdated=solver.addDeliveryPoint(intersectionIndex);
        resultIntersection = plan.computeTour(bestPathUpdated);
        List<String> result2 = new ArrayList<>();

        for(Intersection intersection : resultIntersection) {
            result2.add(intersection.getId());
        }
        System.out.println("finished");

        assertEquals(
                Arrays.asList("208769457, 342868447, 208769312, 208769295, 208769334, 48830474, 48830471, 48830470, 1368674811, 1368674810, 55463805, 26317234, 26317228, 26317232, 26317229, 26317233, 25611760, 250042857, 25336179, 250042857, 25611760, 26317233, 26057084, 26079654, 2587460577, 26086129, 26086128, 208769112, 208769120, 208769133, 208769083, 208769145, 208769180, 208769163, 208769047, 208769039, 208769499, 208769457, 208769457").toString(),
                result2.toString()
        );

        bestPathUpdated=solver.deleteDeliveryPoint(intersectionIndex);
        resultIntersection = plan.computeTour(bestPathUpdated);
        List<String> result3 = new ArrayList<>();

        for(Intersection intersection : resultIntersection) {
            result3.add(intersection.getId());
        }
        System.out.println("finished");

        assertEquals(
                Arrays.asList("208769457, 342868447, 208769312, 208769295, 208769334, 48830474, 48830471, 48830470, 1368674811, 1368674810, 55463805, 26317234, 26317228, 26317232, 26317229, 26317233, 25611760, 250042857, 25336179, 250042857, 25611760, 26317233, 26317229, 26317232, 26317228, 26317234, 55463805, 1368674809, 1368674810, 1368674811, 48830470, 48830471, 48830474, 208769334, 208769295, 208769312, 342868447, 208769457, 208769457").toString(),
                result3.toString()
        );



    }
}
