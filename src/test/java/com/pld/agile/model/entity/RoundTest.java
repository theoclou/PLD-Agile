package com.pld.agile.model.entity;

import java.io.FileNotFoundException;

import javax.management.InstanceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.pld.agile.model.graph.Plan;

class RoundTest {

    @Test
    public void testLoadRequests_ValidFile() throws Exception {
        Round round = new Round();
        Plan plan = new Plan();

        // Initialize valid intersections
        Intersection intersection1 = new Intersection();
        intersection1.initialisation("239603465", 45.752098, 4.902107);

        Intersection intersection2 = new Intersection();
        intersection2.initialisation("1368674802", 45.75492, 4.8753004);

        Intersection intersection3 = new Intersection();
        intersection3.initialisation("26084216", 45.731358, 4.833629);

        Intersection intersection4 = new Intersection();
        intersection4.initialisation("222220", 45.731352, 4.833629);

        // Add the intersections to the plan
        plan.addIntersection(intersection1);
        plan.addIntersection(intersection2);
        plan.addIntersection(intersection3);
        plan.addIntersection(intersection4);

        // Initialize the round with the plan
        round.init(2, plan);

        // Load the valid XML File
        round.loadRequests("src/test/java/com/pld/agile/model/entity/roundTestValidData.xml");

        // Verify that the deliveryRequest are loaded correctly
        assertEquals(3, round.getDeliveryRequestList().size());
    }


    @Test
    public void testLoadRequests_FileNotFound() throws Exception {
        Round round = new Round();
        Plan plan = new Plan();
        round.init(2, plan);

        // Test if an exception is thrown in case of file not found
        Exception exception = assertThrows(FileNotFoundException.class, () -> round.loadRequests("non_existent_file.xml"));

        assertTrue(exception.getMessage().contains("non_existent_file.xml"));
    }

    @Test
    void testInvalidFormat() {
        Plan plan = new Plan();
        String filePath = "src/test/java/com/pld/agile/model/entity/roundTestInvalidFormat.xml";

        Exception exception = assertThrows(Exception.class, () -> plan.readXml(filePath));

        assertTrue(exception.getMessage().contains("Malformed XML file"),
                "Error message does not match for a malformed XML file.");
    }

    @Test
    public void testLoadRequests_IntersectionNotFound() throws Exception {
        Round round = new Round();
        Plan plan = new Plan();

        // Only add a few intersections to the plan
        Intersection intersection1 = new Intersection();
        intersection1.initialisation("239603465", 45.752098, 4.902107);
        plan.addIntersection(intersection1); // Missing 1368674802 et 26084216

        Intersection intersection2 = new Intersection();
        intersection2.initialisation("222220", 45.752097, 4.902107);
        plan.addIntersection(intersection2);

        // Initialize the round with the plan
        round.init(2, plan);

        // Capture the exception
        Exception exception = assertThrows(InstanceNotFoundException.class, () -> {
            round.loadRequests("src/test/java/com/pld/agile/model/entity/roundTestValidData.xml");
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("The intersection '1368674802' doesn't exist!"));
    }
}