package com.pld.agile.model;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Map {
    private List<Section> sections = new ArrayList<>();
    private List<Intersection> intersections = new ArrayList<>();

    public void readXml(String filePath) {
        try {
            File xmlFile = new File(filePath);

            // Vérification si le fichier existe
            if (!xmlFile.exists()) {
                throw new FileNotFoundException("Le fichier '" + filePath + "' est introuvable.");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Reading the intersections
            NodeList intersectionElements = document.getElementsByTagName("noeud");
            for (int i = 0; i < intersectionElements.getLength(); i++) {
                Element element = (Element) intersectionElements.item(i);
                String id = element.getAttribute("id");
                double latitude = Double.parseDouble(element.getAttribute("latitude"));
                double longitude = Double.parseDouble(element.getAttribute("longitude"));

                // Create the Intersection objects
                Intersection intersection = new Intersection();
                intersection.initialisation(id, latitude, longitude);
                intersections.add(intersection);
            }

            // Reading the sections
            NodeList sectionElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < sectionElements.getLength(); i++) {
                Element element = (Element) sectionElements.item(i);
                int originId = Integer.parseInt(element.getAttribute("origine"));
                int destinationId = Integer.parseInt(element.getAttribute("destination"));
                double length = Double.parseDouble(element.getAttribute("longueur"));
                String name = element.getAttribute("nomRue");

                // Create the Section objects
                Section section = new Section();
                section.initialisation(originId,destinationId,name,length);
                sections.add(section);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }
}