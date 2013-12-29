package de.metal_land.tsp;

import lombok.Getter;
import lombok.extern.java.Log;

import java.util.*;

/**
 * Represents a Set of Nodes in a fixed sequence.
 * @author nieh
 */
@Log
public class Route implements Comparable<Route>{
    final private List<Node> route;

    /**
     * The complete distance of the route as Roundtrip.
     */
    @Getter
    final private Integer distance;

    public Route(List<Node> route){
        this.route = route;
        this.distance = calculateDistance();
    }

    /**
     * Calculates the complete distance of the round trip.
     * @return The calculated Distance.
     */
    private int calculateDistance(){
        Node lastNode = null;
        int distance = 0;

        for (Node node : getRoute()) {
            if(lastNode == null){
                lastNode = node;
                continue;
            }

            distance += lastNode.distanceTo(node);
            lastNode = node;
        }

        if (lastNode != null) {
            distance += lastNode.distanceTo(route.get(0));
        }

        return distance;
    }

    /**
     * Calculates the best neighbor of the route.
     * @return The bests neighbor.
     */
    public Route getBestNeighbor(){
        List<Route> neighbors = new ArrayList<>();
        List<Node> route = getRoute();

        for(int i=0;i<route.size()-1;i++){
            List<Node> routeCopy = new ArrayList<>(route);
            Node n = routeCopy.get(i);
            routeCopy.set(i, routeCopy.get(i+1));
            routeCopy.set(i+1, n);

            neighbors.add(new Route(routeCopy));
        }
        Collections.sort(neighbors);

        return neighbors.get(0);
    }

    public Route getBestNeighbor(Deque<Node[]> tabuList){
        List<Route> neighbors = new ArrayList<>();
        List<Node> route = getRoute();
        Map<Route, Node[]> changes = new HashMap<>();

        for(int i=0;i<route.size()-1;i++){
            List<Node> routeCopy = new ArrayList<>(route);

            boolean inList = false;
            for (Node[] nodes : tabuList) {
                inList = Arrays.equals(nodes, new Node[]{routeCopy.get(i), routeCopy.get(i + 1)})
                        | Arrays.equals(nodes, new Node[]{routeCopy.get(i + 1), routeCopy.get(i)});

                if(inList){
                    break;
                }
            }

            if(!inList){
                Node n = routeCopy.get(i);
                routeCopy.set(i, routeCopy.get(i+1));
                routeCopy.set(i+1, n);

                Route newRoute = new Route(routeCopy);
                changes.put(newRoute, new Node[]{routeCopy.get(i), routeCopy.get(i + 1)});

                neighbors.add(newRoute);
            }
        }
        Collections.sort(neighbors);
        tabuList.addFirst(changes.get(neighbors.get(0)));

        return neighbors.get(0);
    }

    /**
     * @return A copy of the route as List.
     */
    public List<Node> getRoute() {
        return new LinkedList<>(route);
    }

    @Override
    public int compareTo(Route o) {
        return getDistance().compareTo(o.getDistance());
    }
}
