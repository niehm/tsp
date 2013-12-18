import com.sun.javafx.binding.StringFormatter;
import de.metal_land.tsp.Node;
import de.metal_land.tsp.Route;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Date: 12/18/13
 *
 * @author nieh
 */
@Log
public class Main {

    final private static LinkedList<Node> nodes = new LinkedList<>();
    final private static Map<Node, Map<Node, Integer>> distances = new HashMap<>();
    private static String name ="";

    public static void main(String args[]){
        readFromFile();
        calculateDistances();
        Route route = new Route();
        route.greedy(nodes, distances);

        Main.log.info(String.format("Distance of Route: %d%n", route.calculateDistance()));
        Main.log.info(route.getRoute().toString());
    }

    public static void readFromFile(){
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


    private static void calculateDistances(){
        for (Node node : nodes) {
            Map<Node, Integer> nodeXDistance = new HashMap<>(nodes.size()-1);
            distances.put(node, nodeXDistance);

            for (Node toNode : nodes) {
                nodeXDistance.put(toNode, node.distanceTo(toNode));
            }

        }

    }
}
