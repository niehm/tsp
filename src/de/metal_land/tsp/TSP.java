package de.metal_land.tsp;

import lombok.*;
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
    @Getter(AccessLevel.NONE)
    private Gui.DataChangedEventListener listener = null;
    final private  LinkedList<Node> nodes = new LinkedList<>();
    final private  Map<Node, Map<Node, Integer>> distances = new HashMap<>();
    private  String name = "";

    @Setter(AccessLevel.NONE)
    private Route bestRoute;

    @NonNull
    private int tabuListMaxSize = 5000;

    @NonNull
    private int maxBadRoutes = 3000;

    public static void main(String args[]){
        TSP problem = new TSP();
        problem.readFromFile(new File("att532.tsp"));

        problem.calculateDistances();
        problem.greedy(problem.getNodes().getFirst());

        // Start UI thread
        Gui gui = new Gui(problem);
        new Thread(gui).start();

        TSP.log.info(String.format("Distance of Route: %d", problem.getBestRoute().getDistance()));
        TSP.log.info(problem.getBestRoute().getRoute().toString());

        problem.localSearch();
        TSP.log.info(String.format("Distance of Route after local Search: %d", problem.getBestRoute().getDistance()));

        problem.tabuSearch();
        TSP.log.info(String.format("Distance of Route after tabu Search: %d", problem.getBestRoute().getDistance()));
    }

    /**
     * Reads the Data from the given file.
     * @param srcFile The File to read.
     */
    public void readFromFile(File srcFile){
        try {
            FileInputStream fr = new FileInputStream(srcFile);
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

    /**
     * Calculates the Distance from a Node to any other Node.
     */
    private void calculateDistances(){
        for (Node node : nodes) {
            Map<Node, Integer> nodeXDistance = new HashMap<>(nodes.size()-1);
            distances.put(node, nodeXDistance);

            for (Node toNode : nodes) {
                nodeXDistance.put(toNode, node.distanceTo(toNode));
            }

        }

    }

    /**
     * Generates a route in a greedy way.
     * @param startNode The Node to start from.
     */
    public void greedy(Node startNode){
        List<Node> nodes = new LinkedList<>(getNodes());
        List<Node> routeList = new ArrayList<>();
        if(nodes.contains(startNode)) {
            routeList.add(startNode);
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
            routeList.add(shortestNode);
        }

        setBestRoute(new Route(routeList));
    }

    /**
     * Search in the Neighborhood for better routes.
     */
    public void localSearch(){
        Route neighbor = bestRoute.getBestNeighbor();

        if(bestRoute.compareTo(neighbor) > 0){
            setBestRoute(neighbor);
            localSearch();
        }
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int badRoutes = 0;

    /**
     * Search in the Neighborhood for better routes, using a tabu list for already taken routes.
     */
    public void tabuSearch(){
        Deque<Node[]> tabuList = new LinkedList<>();
        tabuSearch(bestRoute, tabuList);
    }

    /**
     * Search in the Neighborhood for better routes, using a tabu list for already taken routes.
     * @param route The route to optimize.
     * @param tabuList The prohibited changes.
     */
    private void tabuSearch(Route route, Deque<Node[]> tabuList){
        Route neighbor = route.getBestNeighbor(tabuList);

        if(tabuList.size() > tabuListMaxSize){
            tabuList.removeLast();
        }

        if(bestRoute.compareTo(neighbor) > 0){
            setBestRoute(neighbor);
            badRoutes = 0;
        } else {
            routeChanged(neighbor);
            badRoutes++;
        }

        if(badRoutes < maxBadRoutes) {
            tabuSearch(neighbor, tabuList);
        }
    }

    /**
     * Sets the new best route found and fires the route changed event.
     * @param newRoute The new Route.
     */
    private void setBestRoute(Route newRoute){
        bestRoute = newRoute;
        routeChanged(newRoute);
    }

    /**
     * Should be called if the current route has changed. Normally for redrawing.
     * @param route The new current route.
     */
    private void routeChanged(Route route){
        if(listener != null){
            listener.changed(route);
        }
    }
}
