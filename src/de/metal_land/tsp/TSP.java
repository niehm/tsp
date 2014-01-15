package de.metal_land.tsp;

import de.metal_land.Util;
import lombok.*;
import lombok.extern.java.Log;

import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * @author nieh
 */
@Log
@Data
public class TSP {
    @Getter(AccessLevel.NONE)
    private Gui.DataChangedEventListener listener = null;
    final private  LinkedList<Node> nodes = new LinkedList<>();
    final private  Map<Node, TreeMap<Node, Integer>> distances = new HashMap<>();
    private  String name = "";

    @Setter(AccessLevel.NONE)
    private Route bestRoute;

    @NonNull
    private int tabuListMaxSize = 100;

    @NonNull
    private int maxBadRoutes = 2000;

    public static void main(String args[]){
        TSP problem = new TSP();

        ClassLoader cl = TSP.class.getClassLoader();
        InputStream is = cl.getResourceAsStream("att532.tsp");
        if(is == null){
            try {
                is = new FileInputStream("att532.tsp");
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //Template
            }
        }
        problem.readFromFile(is);

        // Start UI thread
        Gui gui = new Gui(problem);
        new Thread(gui).start();

        problem.calculateDistances();
        problem.greedy();

        TSP.log.info(String.format("Distance of Route: %d", problem.getBestRoute().getDistance()));
        TSP.log.info(problem.getBestRoute().getRoute().toString());

        problem.tabuSearch();
        TSP.log.info(String.format("Distance of Route after tabu Search: %d", problem.getBestRoute().getDistance()));
        problem.routeChanged(problem.getBestRoute());
    }

    /**
     * Reads the Data from the given file.
     * @param srcFile The File to read.
     */
    public void readFromFile(InputStream srcFile){
        Scanner scanner = new Scanner(srcFile);

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

        setTabuListMaxSize((int) (nodes.size()* 0.15));
    }

    /**
     * Calculates the Distance from a Node to any other Node.
     */
    private void calculateDistances(){
        for (Node node : nodes) {
            TreeMap<Node, Integer> nodeXDistance = new TreeMap<>();

            for (Node toNode : nodes) {
                if(!toNode.equals(node)){
                    nodeXDistance.put(toNode, node.distanceTo(toNode));
                }
            }

            TreeMap<Node, Integer> sortedNodeXDistance = new TreeMap<>(new ValueComparator(nodeXDistance));
            sortedNodeXDistance.putAll(nodeXDistance);
            distances.put(node, sortedNodeXDistance);
        }

    }

    /**
     * Generates a route in a greedy way.
     */
    public void greedy(){
        for(int i = 0; i < nodes.size(); i++){
            List<Node> nodes = new LinkedList<>(getNodes());
            List<Node> routeList = new ArrayList<>();
            Node startNode = nodes.get(i);

            routeList.add(startNode);
            nodes.remove(startNode);

            Node lastNode = routeList.get(0);

            while(!nodes.isEmpty()){
                TreeMap<Node,Integer> distanceMap = distances.get(lastNode);
                Node shortestNode = null;

                for (Node node : distanceMap.navigableKeySet()){
                    if(!routeList.contains(node)) {
                        shortestNode = node;
                        break;
                    }
                }

                nodes.remove(shortestNode);
                routeList.add(shortestNode);
                lastNode = shortestNode;
            }

            Route route = new Route(routeList);

            if(bestRoute == null || route.compareTo(bestRoute) < 0){
                setBestRoute(route);
            }
        }
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int badRoutes = 0;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Route currentRoute = null;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Deque<Node[]> tabuList = new LinkedList<>();

    /**
     * Search in the Neighborhood for better routes, using a tabu list for already taken routes.
     */
    public void tabuSearch(){
        if(currentRoute == null) { currentRoute = bestRoute; }

        currentRoute = optimize(currentRoute.getBestNeighbor(tabuList));

        if(tabuList.size() > tabuListMaxSize){
            tabuList.removeLast();
        }

        if(bestRoute.compareTo(currentRoute) > 0){
            setBestRoute(currentRoute);
            badRoutes = 0;
        } else {
            routeChanged(currentRoute);
            badRoutes++;
        }

        if(badRoutes < maxBadRoutes) {
            tabuSearch();
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

    /**
     * Optimize the route by removing loops.
     * @param route The route to optimize.
     * @return The optimized route.
     */
    private Route optimize(Route route){
        List<Node> routeList = route.getRoute();
        List<Node> modifiedList = new ArrayList<>(routeList.size());
        int distance = 30;

        // checks all nodes or until a loop is resolved
        for (Node currentNode : routeList) {
            int currentIndex = routeList.indexOf(currentNode);

            // don't move over the end of the array
            if(currentIndex >= routeList.size()-distance) {
                distance--;
                if(distance <= 4) { break; }
            }

            int index = currentIndex + 2;
            for(int i=0;i<distance;i++){
                Node node = routeList.get(index);
                Node nodeNext = routeList.get(index+1);

                if(Line2D.linesIntersect(nodeNext.getX(), nodeNext.getY(),
                        node.getX(), node.getY(),
                        currentNode.getX(), currentNode.getY(),
                        routeList.get(currentIndex+1).getX(), routeList.get(currentIndex+1).getY())){
                    List<Node> part1 = Util.sublist(routeList, 0, currentIndex+1);
                    //reverse the looped part
                    List<Node> reversePart = Util.reverseList(Util.sublist(routeList, currentIndex+1, index+1));
                    List<Node> part2 = Util.sublist(routeList, index+1, routeList.size());

                    //assemble the new list
                    modifiedList.addAll(part1);
                    modifiedList.addAll(reversePart);
                    modifiedList.addAll(part2);
                    break;
                }
            }

            if(modifiedList.size()>0) { break; }
        }

        if(modifiedList.size()>0) {
            Route newRoute = new Route(modifiedList);
            log.info(String.format("Old Route: %d  New Route: %d", route.getDistance(), newRoute.getDistance()));
            return optimize(newRoute);
        } else {
            return route;
        }
    }

    /**
     * Sets the maxsize for the tabu search tabulist. Min value is 20.
     * @param size The new size.
     */
    public void setTabuListMaxSize(int size){
        tabuListMaxSize = (size > 20)? size : 20;
    }

    /**
     * A comparator to sort a map by the Value.
     */
    @AllArgsConstructor
    private class ValueComparator implements Comparator<Node> {
        private Map<Node, Integer> base;

        public int compare(Node a, Node b) {
            if (base.get(a) > base.get(b)) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
