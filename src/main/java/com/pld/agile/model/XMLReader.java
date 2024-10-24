package com.pld.agile.model;

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

    @SuppressWarnings("UseSpecificCatch")
    /**
     * Reads an XML file containing the plan of the city and populates the list of
     * intersections and sections. It parses the XML, creates intersection and
     * section objects, and stores them in their respective lists.
     *
     * @param filePath the path to the XML file
     * @throws Exception if an error occurs during the reading or parsing process
     */
    public static Map<String, Object> readXml(String filePath) throws Exception {
        Map<String, Object> result = new HashMap<>();

        List<Intersection> intersections = new ArrayList<>();
        Map<String, Intersection> intersectionMap = new HashMap<>();
        List<Section> sections = new ArrayList<>();
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

        result.put("intersections", intersections);
        result.put("sections", sections);
        result.put("intersectionMap", intersectionMap);

        System.out.println("Nombre d'intersections : " + intersections.size());
        System.out.println("Nombre de tronçons : " + sections.size());

        return result;
        /**
         * Resets the map, clearing all intersections and sections previously loaded.
         */
    }


    public static Map<String, Object> LoadPlanByFile(MultipartFile file){
        Map<String, Object> result;
        Map<String, Object> tempResult = new HashMap<>();
        try {
            result = readXmlbyFile(file);
            if (result.isEmpty() || !result.containsKey("intersections") || !result.containsKey("sections") ) {
                throw new Exception("The XML file is empty or invalid.");
            }
            tempResult = preprocessData((List<Intersection>) result.get("intersections"), (List<Section>) result.get("sections"));
            result.put("indexes", tempResult.get("indexes"));
            result.put("reverseIndexes", tempResult.get("reverseIndexes"));
            result.put("costsMatrix", tempResult.get("costsMatrix"));
        } catch (Exception e) {
            e.printStackTrace();
            result = new HashMap<>();
        }
        return result;
    }

    public static Map<String, Object> LoadPlanByPath(String filePath) throws FileNotFoundException, IllegalArgumentException, InstanceNotFoundException {
        try {
            // Read XML file
            Map<String, Object> result = readXml(filePath);

            // Validate XML content
            if (result == null || result.isEmpty()) {
                throw new IllegalArgumentException("The XML file is empty.");
            }
            if (!result.containsKey("intersections") || !result.containsKey("sections")) {
                throw new IllegalArgumentException("The XML file is missing required elements (intersections or sections).");
            }

            // Type-safe casting with validation
            List<Intersection> intersections;
            List<Section> sections;
            try {
                intersections = (List<Intersection>) result.get("intersections");
                sections = (List<Section>) result.get("sections");

                if (intersections == null || sections == null) {
                    throw new IllegalArgumentException("Intersections or sections data is null.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Invalid data format for intersections or sections.", e);
            }

            // Process the data
            Map<String, Object> processedData = preprocessData(intersections, sections);

            // Add processed data to result
            result.put("indexes", processedData.get("indexes"));
            result.put("reverseIndexes", processedData.get("reverseIndexes"));
            result.put("costsMatrix", processedData.get("costsMatrix"));

            return result;

        } catch (FileNotFoundException e) {
            throw e; // Rethrow FileNotFoundException directly
        } catch (IllegalArgumentException e) {
            throw e; // Rethrow validation errors
        } catch (InstanceNotFoundException e) {
            throw e; // Rethrow InstanceNotFoundException directly
        } catch (Exception e) {
            // Convert unexpected exceptions to IllegalArgumentException
            throw new IllegalArgumentException("Error processing XML file: " + e.getMessage(), e);
        }
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
    public static Map<String, Object> readXmlbyFile(MultipartFile file) throws Exception {
        File tempFile = null;
        Map<String, Object> result = new HashMap<>();

        List<Intersection> intersections = new ArrayList<>();
        Map<String, Intersection> intersectionMap = new HashMap<>();
        List<Section> sections = new ArrayList<>();
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
            throw e;
        } catch (SAXException e) {
            throw new Exception("Malformed XML file: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid numeric value: " + e.getMessage());
        } finally {
            result.put("intersections", intersections);
            result.put("sections", sections);
            result.put("intersectionMap", intersectionMap);

            // Cleanup temporary file in case of exception
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }

        }

        System.out.println("Number of intersections: " + intersections.size());
        System.out.println("Number of sections: " + sections.size());
        return result;
    }


    /**
     * Re-indexes all intersections in the plan based on their IDs.
     */
    private static Map<String, Object> reIndexIntersections(List<Intersection> intersections) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Integer> indexes = new HashMap<>();
        Map<Integer, String> reverseIndexes = new HashMap<>();
        int i = 0;
        for (Intersection intersection : intersections) {
            String id = intersection.getId();
            indexes.put(id, i);
            reverseIndexes.put(i, id);
            i += 1;
        }
        result.put("indexes", indexes);
        result.put("reverseIndexes", reverseIndexes);
        return result;
    }

    /**
     * gets all intersection's ids using the predefined indexes in
     * <code> reIndexIntersections() </code>.
     */
//    private void reverseIndexation() {
//        for (Map.Entry<String, Integer> pair : indexes.entrySet()) {
//            reverseIndexes.put(pair.getValue(), pair.getKey());
//        }
//    }

    /**
     * Initializes the cost matrix for the graph with 0 or infinity values. This
     * method sets the distance from a node to itself as 0 and sets all other
     * distances to infinity.
     */
    private static ArrayList<ArrayList<Double>> initializeCostsMatrix(List<Intersection> intersections) {
        ArrayList<ArrayList<Double>> costsMatrix = new ArrayList<>();

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

        return costsMatrix;
    }

    /**
     * Fills the cost matrix based on the sections read from the XML file.
     */
    private static void fillCostsMatrix(List<Section> sections, Map<String, Integer> indexes, ArrayList<ArrayList<Double>> costsMatrix) {
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
    private static ArrayList<ArrayList<Double>> makeCostsMatrix(List<Intersection> intersections, List<Section> sections, Map<String, Integer> indexes) {
        // Initialize the adjacency matrix with the size of the intersections
        ArrayList<ArrayList<Double>> costsMatrix;

        costsMatrix = initializeCostsMatrix(intersections);
        fillCostsMatrix(sections, indexes, costsMatrix);
        return costsMatrix;
    }

    /**
     * Processes the data by creating indexing the ids of the intersections of the
     * map and creating the cost matrix.
     */
    public static Map<String, Object> preprocessData(List<Intersection> intersections, List<Section> sections) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> tempResult = new HashMap<>();

        tempResult = reIndexIntersections(intersections);
        result.put("indexes", tempResult.get("indexes"));
        result.put("reverseIndexes", tempResult.get("reverseIndexes"));
        result.put("costsMatrix", makeCostsMatrix(intersections, sections, (Map<String, Integer>) result.get("indexes")));

        return result;
    }


}
