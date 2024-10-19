import java.util.*;

public class Graph {
    private Set<Section> sections;
    private Set<Intersection> intersections;

    public Graph(Set<Section> sections, Set<Intersection> intersections) {
        this.sections = sections;
        this.intersections = intersections;
    }

    public List<Intersection> findShortestPath(String startId, String endId) {
        // A map to keep track of the shortest distance from start to each node
        Map<Intersection, Double> distances = new HashMap<>();
        // A map to reconstruct the shortest path
        Map<Intersection, Intersection> previous = new HashMap<>();
        // Priority queue to explore nodes by shortest distance
        PriorityQueue<NodeDistancePair> pq = new PriorityQueue<>(Comparator.comparingDouble(NodeDistancePair::getDistance));

        // Find the starting and ending intersections
        Intersection start = findIntersectionById(startId);
        Intersection end = findIntersectionById(endId);

        if (start == null || end == null) {
            throw new IllegalArgumentException("Start or end intersection not found");
        }

        // Initialize distances
        for (Intersection intersection : intersections) {
            if (intersection.equals(start)) {
                distances.put(intersection, 0.0);  // Distance to start is 0
            } else {
                distances.put(intersection, Double.MAX_VALUE);  // Infinite distance for all other nodes
            }
        }

        // Add the start node to the priority queue
        pq.add(new NodeDistancePair(start, 0.0));

        // Dijkstra's algorithm
        while (!pq.isEmpty()) {
            NodeDistancePair current = pq.poll();
            Intersection currentIntersection = current.getNode();

            // If we reached the destination node, break out of the loop
            if (currentIntersection.equals(end)) {
                break;
            }

            // Explore all neighboring nodes (connected sections)
            for (Section section : getNeighboringSections(currentIntersection)) {
                Intersection neighbor = findIntersectionById(section.getDestinationId());
                if (neighbor == null) continue;

                // Calculate new distance
                double newDist = distances.get(currentIntersection) + section.getLength();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentIntersection);
                    pq.add(new NodeDistancePair(neighbor, newDist));
                }
            }
        }

        // Reconstruct the shortest path
        List<Intersection> path = new ArrayList<>();
        for (Intersection at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);  // Reverse the path to start from the beginning

        // If the start node is not in the path, it means there is no valid path
        if (path.isEmpty() || !path.get(0).equals(start)) {
            return Collections.emptyList();  // No path found
        }

        return path;
    }

    // Helper to get neighboring sections (edges) for a given intersection (node)
    private List<Section> getNeighboringSections(Intersection intersection) {
        List<Section> neighbors = new ArrayList<>();
        for (Section section : sections) {
            if (section.getOriginId().equals(intersection.getId())) {
                neighbors.add(section);
            }
        }
        return neighbors;
    }

    // Helper to find an intersection by its ID
    private Intersection findIntersectionById(String id) {
        for (Intersection intersection : intersections) {
            if (intersection.getId().equals(id)) {
                return intersection;
            }
        }
        return null;
    }
}

// Class to represent a node and its distance in the priority queue
class NodeDistancePair {
    private Intersection node;
    private double distance;

    public NodeDistancePair(Intersection node, double distance) {
        this.node = node;
        this.distance = distance;
    }

    public Intersection getNode() {
        return node;
    }

    public double getDistance() {
        return distance;
    }
}
