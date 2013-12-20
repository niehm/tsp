package de.metal_land.tsp;

import lombok.Data;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 * @author nieh
 */
@Log
@Data
public class TSP {

    final private  LinkedList<Node> nodes = new LinkedList<>();
    final private  Map<Node, Map<Node, Integer>> distances = new HashMap<>();
    private  String name ="";

    public static void main(String args[]){
        TSP problem = new TSP();
        problem.readFromFile();
        problem.calculateDistances();
        Route route = problem.greedy(problem.getNodes().getFirst());

        TSP.log.info(String.format("Distance of Route: %d%n", route.getDistance()));
        TSP.log.info(route.getRoute().toString());
    }

    public void readFromFile(){
        try {
            FileInputStream fr = new FileInputStream(new File("att532.tsp"));
            Scanner scanner = new Scanner(fr);

            boolean readCoordinates = false;
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(line.equals("NODE_COORD_SECTION")) {
                    readCoordinates = true;
                    continue;
                } else if(line.equals("EOF")){
                    break;
                }

                if(!readCoordinates){
                    String[] splitted = line.split(" : ");
                    if(splitted[0].equals("NAME")) {
                        name = splitted[1];
                    }
                } else {
                    String[] splitted = line.split(" ");
                    nodes.add(new Node(splitted[0], Integer.parseInt(splitted[1]), Integer.parseInt(splitted[2])));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void calculateDistances(){
        for (Node node : nodes) {
            Map<Node, Integer> nodeXDistance = new HashMap<>(nodes.size()-1);
            distances.put(node, nodeXDistance);

            for (Node toNode : nodes) {
                nodeXDistance.put(toNode, node.distanceTo(toNode));
            }

        }

    }

    public Route greedy(Node startNode){
        Route route = new Route();
        List<Node> nodes = new LinkedList<Node>(getNodes());
        Collections.copy(nodes, getNodes());
        if(nodes.contains(startNode)) {
            route.addNode(startNode);
            nodes.remove(startNode);
        }

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
            route.addNode(shortestNode);
        }

        return route;
    }
}
