import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class Intersection {
    private String id;
    private double latitude;
    private double longitude;

    public Intersection(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and toString()
    public String getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return "Intersection{id='" + id + "', latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}

class Section {
    private String origin;
    private String destination;
    private double length;
    private String streetName;

    public Section(Intersection origin, Intersection destination, String name, double length ) {
        this.origin = origin;
        this.destination = destination;
        this.name = name;
        this.length = length;
    }

    // Getters and toString()
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getLength() { return length; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Section{origin='" + origin + "', destination='" + destination + 
                "', name=" + name + ", length='" + length + "'}";
    }
}

class Graph {
    private Map<Intersection,List<Section>> adjencyList = new Map<>();

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
