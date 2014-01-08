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
            distance += lastNode.distanceTo(getRoute().get(0));
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

    /**
     * Get the best neigbhor, who is not on the tabu list.
     * @param tabuList The list with forbidden changes.
     * @return The bests neighbor.
     */
    public Route getBestNeighbor(final Deque<Node[]> tabuList){
        final List<Route> neighbors = new ArrayList<>();
        final List<Node> route = getRoute();
        final Map<Route, Node[]> changes = new HashMap<>();

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

        List<Thread> threadList = new ArrayList<>(8);
        for(int i=0; i<8;i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Route newRoute = addBestRandomNeighbor(tabuList, changes);
                    synchronized (route){
                        neighbors.add(newRoute);
                    }
                }
            });
            thread.start();
            threadList.add(thread);
        }
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //Template
            }
        }


        Collections.sort(neighbors);
        tabuList.addFirst(changes.get(neighbors.get(0)));

        return neighbors.get(0);
    }

    /**
     * Adds some random Neighbors to the List.
     * @param tabuList The list with forbidden changes.
     * @param changes A map to which the changed values should be added.
     * @return A random neighbor route.
     */
    private Route addBestRandomNeighbor(Deque<Node[]> tabuList, Map<Route, Node[]> changes) {
        List<Route> neighbors = new ArrayList<>();
        List<Node> route = getRoute();
        Map<Route, Node[]> innerChanges = new HashMap<>();

        // Add some random generated routes
        final Random rnd = new Random();
        for(int i = 0; i<getRoute().size() * 0.3; i++){
            List<Node> routeCopy = new ArrayList<>(route);
            int first = rnd.nextInt(routeCopy.size());
            int second = rnd.nextInt(routeCopy.size());

            boolean inList = false;
            for (Node[] nodes : tabuList) {
                inList = Arrays.equals(nodes, new Node[]{routeCopy.get(first), routeCopy.get(second)})
                        | Arrays.equals(nodes, new Node[]{routeCopy.get(second), routeCopy.get(first)});

                if(inList){
                    break;
                }
            }

            if(!inList){
                Node n = routeCopy.get(first);
                routeCopy.set(first, routeCopy.get(second));
                routeCopy.set(second, n);

                Route newRoute = new Route(routeCopy);
                innerChanges.put(newRoute, new Node[]{routeCopy.get(first), routeCopy.get(second)});

                neighbors.add(newRoute);
            }
        }

        Collections.sort(neighbors);
        synchronized(this.route){
            changes.put(neighbors.get(0), innerChanges.get(neighbors.get(0)));
        }

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
