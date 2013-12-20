package de.metal_land.tsp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a Set of Nodes in a fixed sequence.
 * @author nieh
 */
@NoArgsConstructor
@Log
public class Route {
    @Getter
    final private List<Node> route = new LinkedList<>();

    /**
     * Add a Node at the end of the Route.
     * @param node
     */
    public void addNode(Node node){
        route.add(node);
    }

    /**
     * Returns the complete distance of the round trip.
     * @return
     */
    public int getDistance(){
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
}
