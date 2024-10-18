

public class Graph {
    private Set<Section> sections = new HashSet<>;
    private Set<Intersection> intersections = new HashSet<>;

    public void readXml(String filePath) {
        try {
            File xmlFile = new File(filePath);
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
                String originId = element.getAttribute("origine");
                String destinationId = element.getAttribute("destination");
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
}