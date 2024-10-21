package com.pld.agile.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanTest {

    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = new Plan();
    }

    @Test
    void testReadXmlWithValidFile() throws Exception {
        String filePath = "src/test/java/com/pld/agile/model/planTestValidData.xml";  // Path to the XML file

        // Call of the readXml method
        plan.readXml(filePath);

        // Checking intersections
        List<Intersection> intersections = plan.getIntersections();
        assertEquals(3, intersections.size(), "Incorrect number of intersection.");

        Intersection intersection = intersections.stream()
                .filter(i -> i.getId().equals("25175791"))
                .findFirst()
                .orElse(null);

        assertNotNull(intersection, "Intersection with the ID 25175791 not found.");
        assertEquals(45.75406, intersection.getLatitude(), 0.00001);
        assertEquals(4.857418, intersection.getLongitude(), 0.00001);

        // Vérification des sections
        List<Section> sections = plan.getSections();
        assertEquals(2, sections.size(), "Incorrect number of section.");

        Section section = sections.stream()
                .filter(s -> "25175791".equals(s.getOrigin()) && "25175778".equals(s.getDestination()))
                .findFirst()
                .orElse(null);

        assertNotNull(section, "Section with origin 25175791 and destination 25175778 not found.");
        assertEquals("Rue Danton", section.getName());
        assertEquals(69.979805, section.getLength(), 0.00001);
    }

    @Test
    void testReadXmlFileNotFound() {
        String invalidFilePath = "src/test/java/com/pld/agile/model//fichierNotExist.xml";

        // Test if an exception is thrown in case of file not found
        Exception exception = assertThrows(FileNotFoundException.class, () -> plan.readXml(invalidFilePath));

        assertTrue(exception.getMessage().contains("fichierNotExist.xml"));
    }

    @Test
    void testReadXmlWithInvalidData() {
        Plan plan = new Plan();
        String filePath = "src/test/java/com/pld/agile/model/planTestInvalidData.xml";

        Exception exception = assertThrows(NumberFormatException.class, () -> plan.readXml(filePath));

        assertTrue(exception.getMessage().contains("Invalid numeric value"),
                "Error message does not match for invalid numeric data.");
    }
}