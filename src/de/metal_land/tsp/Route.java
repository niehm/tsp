package de.metal_land.tsp;

import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Set of Nodes in a fixed sequence.
 * @author nieh
 */
@Log
public class Route {
    @Getter
    final private List<Node> route;

    @Getter
    final private int distance;

    public Route(List<Node> route){
        this.route = route;
        this.distance = calculateDistance();
    }

    /**
     * Calculates the complete distance of the round trip.
     * @return
     */
    private int calculateDistance(){
        Node lastNode = null;
        int distance = 0;

        for (Node node : route) {
            if(lastNode == null){
                lastNode = node;
                continue;
            }

            distance += lastNode.distanceTo(node);
            lastNode = node;
        }

        distance += lastNode.distanceTo(route.get(0));
        return distance;
    }

    public Iterable<Route> getNeighbors(){
        List<Route> neighbors = new ArrayList<>();
        return neighbors;
    }
}
