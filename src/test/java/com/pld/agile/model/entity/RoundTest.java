package com.pld.agile.model.entity;

import com.pld.agile.model.graph.Plan;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {

    @Test
    public void testLoadRequests_ValidFile() throws Exception {
        Round round = new Round();
        Plan plan = new Plan();

        // Initialiser des intersections valides
        Intersection intersection1 = new Intersection();
        intersection1.initialisation("239603465", 45.752098, 4.902107);

        Intersection intersection2 = new Intersection();
        intersection2.initialisation("1368674802", 45.75492, 4.8753004);

        Intersection intersection3 = new Intersection();
        intersection3.initialisation("26084216", 45.731358, 4.833629);

        // Ajouter les intersections au plan
        plan.addIntersection(intersection1);
        plan.addIntersection(intersection2);
        plan.addIntersection(intersection3);

        // Initialiser le round avec le plan
        round.init(new ArrayList<>(), plan);

        // Charger le fichier XML valide
        round.loadRequests("src/test/java/com/pld/agile/model/entity/roundTestValidData.xml");

        // Vérifier que les DeliveryRequest sont bien ajoutés
        assertEquals(3, round.getDeliveryRequestList().size());
    }


    @Test
    public void testLoadRequests_FileNotFound() throws Exception {
        Round round = new Round();
        Plan plan = new Plan();
        round.init(new ArrayList<>(), plan);

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

        // Ajouter seulement certaines intersections
        Intersection intersection1 = new Intersection();
        intersection1.initialisation("239603465", 45.752098, 4.902107);
        plan.addIntersection(intersection1); // Missing 1368674802 et 26084216

        // Initialiser le round avec le plan
        round.init(new ArrayList<>(), plan);

        // Capturer l'exception
        Exception exception = assertThrows(InstanceNotFoundException.class, () -> {
            round.loadRequests("src/test/java/com/pld/agile/model/entity/roundTestValidData.xml");
        });

        // Vérifier le message de l'exception
        assertTrue(exception.getMessage().contains("The intersection '1368674802' doesn't exist !"));
    }

}