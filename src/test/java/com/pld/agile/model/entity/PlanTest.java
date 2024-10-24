package com.pld.agile.model.entity;

import com.pld.agile.model.graph.Plan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.pld.agile.model.XMLReader;

class PlanTest {

    private XMLReader reader;

    @BeforeEach
    void setUp() {
        reader = new XMLReader();
    }

    @Test
    void testReadXmlWithValidFile() throws Exception {
        String filePath = "src/test/java/com/pld/agile/model/entity/planTestValidData.xml";  // Path to the XML file

        // Call of the readXml method
        reader.readXml(filePath);

        // Checking intersections
        List<Intersection> intersections = reader.getIntersections();
        assertEquals(4, intersections.size(), "Incorrect number of intersection.");

        Intersection intersection = intersections.stream()
                .filter(i -> i.getId().equals("208822503"))
                .findFirst()
                .orElse(null);

        assertNotNull(intersection, "Intersection with the ID 208822503 not found.");
        assertEquals(45.76229, intersection.getLatitude(), 0.00001);
        assertEquals(4.869225, intersection.getLongitude(), 0.00001);

        // Vérification des sections
        List<Section> sections = reader.getSections();
        assertEquals(2, sections.size(), "Incorrect number of section.");

        Section section = sections.stream()
                .filter(s -> "21993013".equals(s.getOrigin()) && "21992980".equals(s.getDestination()))
                .findFirst()
                .orElse(null);

        assertNotNull(section, "Section with origin 21993013 and destination 21992980 not found.");
        assertEquals("Petite Rue de Monplaisir", section.getName());
        assertEquals(75.947624, section.getLength(), 0.00001);
    }

    @Test
    void testReadXmlFileNotFound() {
        String invalidFilePath = "src/test/java/com/pld/agile/model/entity/fichierNotExist.xml";

        // Test if an exception is thrown in case of file not found
        Exception exception = assertThrows(FileNotFoundException.class, () -> reader.readXml(invalidFilePath));

        assertTrue(exception.getMessage().contains("fichierNotExist.xml"));
    }

    @Test
    void testReadXmlWithInvalidData() {
        Plan plan = new Plan();
        String filePath = "src/test/java/com/pld/agile/model/entity/planTestInvalidData.xml";

        Exception exception = assertThrows(NumberFormatException.class, () -> reader.readXml(filePath));

        assertTrue(exception.getMessage().contains("Invalid numeric value"),
                "Error message does not match for invalid numeric data.");
    }

    @Test
    void testNoIntersectionCorrespondingToSection() {
        Plan plan = new Plan();
        String filePath = "src/test/java/com/pld/agile/model/entity/planTestNoCorrespondingIntersection.xml";

        Exception exception = assertThrows(InstanceNotFoundException.class, () -> reader.readXml(filePath));

        assertTrue(exception.getMessage().contains("The XML file is missing required origin or destination intersections"),
                "Error message does not match for not found instance.");
    }

    @Test
    void testInvalidFormat() {
        Plan plan = new Plan();
        String filePath = "src/test/java/com/pld/agile/model/entity/planTestInvalidFormat.xml";

        Exception exception = assertThrows(Exception.class, () -> reader.readXml(filePath));

        assertTrue(exception.getMessage().contains("Malformed XML file"),
                "Error message does not match for a malformed XML file.");
    }
}