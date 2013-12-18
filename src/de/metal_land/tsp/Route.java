package de.metal_land.tsp;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Date: 12/18/13
 *
 * @author nieh
 */
@NoArgsConstructor
public class Route {
    @Getter
    final private List<Node> route = new LinkedList<>();

    public void addNode(Node node){
        route.add(node);
    }

    public void greedy(LinkedList<Node> nodes, Map<Node,Map<Node,Integer>> distances){
        route.clear();
        route.add(nodes.getFirst());
        nodes.removeFirst();

        while(!nodes.isEmpty()){
            Node lastNode = nodes.get(nodes.size()-1);
            Map<Node,Integer> distanceMap = distances.get(lastNode);
            Node shortestNode = null;
            Integer shortestDistance = Integer.MAX_VALUE;

            for (Node node : distanceMap.keySet()) {
                if(distanceMap.get(node) < shortestDistance) {
                    shortestDistance = distanceMap.get(node);
                    shortestNode = node;
                }
            }

            nodes.remove(shortestNode);
            route.add(shortestNode);
        }
    }

    public int calculateDistance(){
        Node lastNode = null;
        int distance = 0;

        for (Node node : route) {
            if(lastNode == null){
                lastNode = node;
                continue;
            }

            distance += lastNode.distanceTo(node);
        }

        return distance;
    }
}
