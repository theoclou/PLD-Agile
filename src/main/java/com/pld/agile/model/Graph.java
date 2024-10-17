package com.pld.agile.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.text.Segment;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;
import java.util.Map;

class Graph {
    private Map<Intersection, List<Section>> adjacencyList = new Map<>();

    public void loadFromXml(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList nodeElements = document.getElementsByTagName("noeud");
            for (int i = 0; i < nodeElements.getLength(); i++) {
                Element element = (Element) nodeElements.item(i);
                String id = element.getAttribute("id");
                double latitude = Double.parseDouble(element.getAttribute("latitude"));
                double longitude = Double.parseDouble(element.getAttribute("longitude"));
                nodes.add(new Node(id, latitude, longitude));
            }

            NodeList segmentElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < segmentElements.getLength(); i++) {
                Element element = (Element) segmentElements.item(i);
                String origin = element.getAttribute("origine");
                String destination = element.getAttribute("destination");
                double length = Double.parseDouble(element.getAttribute("longueur"));
                String streetName = element.getAttribute("nomRue");
                segments.add(new Segment(origin, destination, length, streetName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters for nodes and segments
    public List<Node> getNodes() { return nodes; }
    public List<Segment> getSegments() { return segments; }
}