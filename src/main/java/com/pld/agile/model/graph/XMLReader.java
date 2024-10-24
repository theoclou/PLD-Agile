package com.pld.agile.model.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pld.agile.model.entity.Intersection;
import com.pld.agile.model.entity.Section;

public class XMLReader {
    @SuppressWarnings("FieldMayBeFinal")
    private List<Section> sections = new ArrayList<>();
    @SuppressWarnings("FieldMayBeFinal")
    private List<Intersection> intersections = new ArrayList<>();
    private Map<String, Intersection> intersectionMap = new HashMap<>();
    @SuppressWarnings("FieldMayBeFinal")
    private Map<String, Integer> indexes = new HashMap<>();
    private Map<Integer, String> reverseIndexes = new HashMap<>();
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<ArrayList<Double>> costsMatrix = new ArrayList<>();

    @SuppressWarnings("UseSpecificCatch")
    /**
     * Reads an XML file containing the plan of the city and populates the list of
     * intersections and sections. It parses the XML, creates intersection and
     * section objects, and stores them in their respective lists.
     *
     * @param filePath the path to the XML file
     * @throws Exception if an error occurs during the reading or parsing process
     */
    public void readXml(String filePath) throws Exception {
        try {
            File xmlFile = new File(filePath);

            // Check if the file exists
            if (!xmlFile.exists()) {
                throw new FileNotFoundException("The file '" + filePath + "' is not found.");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Reading the intersections
            NodeList intersectionElements = document.getElementsByTagName("noeud");
            for (int i = 0; i < intersectionElements.getLength(); i++) {
                Element element = (Element) intersectionElements.item(i);
                String id = element.getAttribute("id");
                try {
                    double latitude = Double.parseDouble(element.getAttribute("latitude"));
                    double longitude = Double.parseDouble(element.getAttribute("longitude"));

                    // Create the Intersection objects
                    Intersection intersection = new Intersection();
                    intersection.initialisation(id, latitude, longitude);
                    intersectionMap.put(id, intersection);
                    intersections.add(intersection);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Invalid numeric value in an intersection : " + e.getMessage());
                }
            }

            // Reading the sections
            NodeList sectionElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < sectionElements.getLength(); i++) {
                Element element = (Element) sectionElements.item(i);
                try {
                    String originId = element.getAttribute("origine");
                    String destinationId = element.getAttribute("destination");
                    double length = Double.parseDouble(element.getAttribute("longueur"));
                    String name = element.getAttribute("nomRue");

                    // Create the Section objects
                    Section section = new Section();
                    section.initialisation(originId, destinationId, name, length);
                    sections.add(section);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Invalid numeric value in a section: " + e.getMessage());
                }
            }

            for (int i = 0; i < sections.size(); i++) {
                Boolean originFind = false;
                Boolean destinationFind = false;
                for (int j = 0; j < intersections.size(); j++) {
                    if (intersections.get(j).getId().equals(sections.get(i).getOrigin())) {
                        originFind = true;
                    }
                    if (intersections.get(j).getId().equals(sections.get(i).getDestination())) {
                        destinationFind = true;
                    }
                }
                if (!originFind || !destinationFind) {
                    throw new InstanceNotFoundException(
                            "The XML file is missing required origin or destination intersections.");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e; // Propagate exception if file not found
        } catch (SAXException e) {
            // Captures errors related to malformed XML parsing
            throw new Exception("Malformed XML file : " + e.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw e;
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        System.out.println("Nombre d'intersections : " + intersections.size());
        System.out.println("Nombre de tronçons : " + sections.size());
        /**
         * Resets the map, clearing all intersections and sections previously loaded.
         */
    }

    public void resetMap() {
        intersectionMap.clear();
        intersections.clear();
        sections.clear();
    }

    // ----------------------------------------------------------------------
    /**
     * Reads an XML file from a {@code MultipartFile} and parses its content into
     * the
     * list of intersections and sections.
     *
     * @param file the uploaded XML file
     * @throws Exception if an error occurs during the reading or parsing process
     */
    public void readXmlbyFile(MultipartFile file) throws Exception {
        File tempFile = null;
        try {
            // Convert MultipartFile to a temporary file
            tempFile = File.createTempFile("uploaded-", ".xml");
            file.transferTo(tempFile);

            // Parse the XML document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(tempFile);

            // Read intersections
            NodeList intersectionElements = document.getElementsByTagName("noeud");
            for (int i = 0; i < intersectionElements.getLength(); i++) {
                Element element = (Element) intersectionElements.item(i);
                String id = element.getAttribute("id");
                double latitude = Double.parseDouble(element.getAttribute("latitude"));
                double longitude = Double.parseDouble(element.getAttribute("longitude"));

                Intersection intersection = new Intersection();
                intersection.initialisation(id, latitude, longitude);
                intersectionMap.put(id, intersection);
                intersections.add(intersection);
            }

            // Read sections
            NodeList sectionElements = document.getElementsByTagName("troncon");
            for (int i = 0; i < sectionElements.getLength(); i++) {
                Element element = (Element) sectionElements.item(i);
                String originId = element.getAttribute("origine");
                String destinationId = element.getAttribute("destination");
                double length = Double.parseDouble(element.getAttribute("longueur"));
                String name = element.getAttribute("nomRue");

                Section section = new Section();
                section.initialisation(originId, destinationId, name, length);
                sections.add(section);
            }

            // Validate that all sections have valid intersections
            for (Section section : sections) {
                boolean originFound = intersections.stream().anyMatch(i -> i.getId().equals(section.getOrigin()));
                boolean destinationFound = intersections.stream()
                        .anyMatch(i -> i.getId().equals(section.getDestination()));
                if (!originFound || !destinationFound) {
                    throw new InstanceNotFoundException(
                            "The XML file is missing required origin or destination intersections.");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            resetMap(); // Clear data on file not found
            throw e;
        } catch (SAXException e) {
            resetMap(); // Clear data on XML parsing error
            throw new Exception("Malformed XML file: " + e.getMessage());
        } catch (NumberFormatException e) {
            resetMap(); // Clear data on numeric parsing error
            throw new NumberFormatException("Invalid numeric value: " + e.getMessage());
        } finally {
            // Cleanup temporary file in case of exception
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        System.out.println("Number of intersections: " + intersections.size());
        System.out.println("Number of sections: " + sections.size());
    }

    public Map<String, Intersection> getIntersectionMap() {
        return intersectionMap;
    }

    /**
     * Re-indexes all intersections in the plan based on their IDs.
     */
    private void reIndexIntersections() {
        int i = 0;
        for (Intersection intersection : intersections) {
            String id = intersection.getId();
            indexes.put(id, i);
            reverseIndexes.put(i, id);
            i += 1;
        }
    }

    /**
     * gets all intersection's ids using the predefined indexes in
     * <code> reIndexIntersections() </code>.
     */
    private void reverseIndexation() {
        for (Map.Entry<String, Integer> pair : indexes.entrySet()) {
            reverseIndexes.put(pair.getValue(), pair.getKey());
        }
    }

    /**
     * Initializes the cost matrix for the graph with 0 or infinity values. This
     * method sets the distance from a node to itself as 0 and sets all other
     * distances to infinity.
     */
    private void initializeCostsMatrix() {
        int size = intersections.size();
        for (int i = 0; i < size; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    row.add(0.0); // Distance to self is 0
                } else {
                    row.add(Double.MAX_VALUE); // Initialize with a large value to indicate no direct connection
                }
            }
            costsMatrix.add(row);
        }
    }

    /**
     * Fills the cost matrix based on the sections read from the XML file.
     */
    private void fillCostsMAtrix() {
        // Set the costs based on the sections
        for (Section section : sections) {
            String originId = section.getOrigin();
            String destinationId = section.getDestination();
            double length = section.getLength();

            int originIndex = indexes.get(originId);
            int destinationIndex = indexes.get(destinationId);

            // Set the distance (or cost) for the matrix
            costsMatrix.get(originIndex).set(destinationIndex, length);
        }
    }

    /**
     * makes the cost matrix using the values extarcted from the file
     */
    private void makeCostsMatrix() {
        // Initialize the adjacency matrix with the size of the intersections
        initializeCostsMatrix();
        fillCostsMAtrix();
    }

    /**
     * Processes the data by creating indexing the ids of the intersections of the
     * map and creating the cost matrix.
     */
    public void preprocessData() {
        reIndexIntersections();
        reverseIndexation();
        makeCostsMatrix();
    }


    public List<Section> getSections() {
        return this.sections;
    }

   

    public List<Intersection> getIntersections() {
        return this.intersections;
    }

    

    public Map<String,Integer> getIndexes() {
        return this.indexes;
    }

    

    public Map<Integer,String> getReverseIndexes() {
        return this.reverseIndexes;
    }

    

    public ArrayList<ArrayList<Double>> getCostsMatrix() {
        return this.costsMatrix;
    }



}
