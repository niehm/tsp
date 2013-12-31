package de.metal_land.tsp;

import lombok.*;
import lombok.extern.java.Log;

/**
 * @author nieh
 */
@Data
@Log
@AllArgsConstructor
@NoArgsConstructor
public class Node implements Comparable<Node>{
    @Setter(AccessLevel.NONE)
    private String name = "";

    @Setter(AccessLevel.NONE)
    private int x = 0;

    @Setter(AccessLevel.NONE)
    private int y = 0;

    /**
     * Calculates the distance form this Node to the given target Node.
     * @param node The target Node.
     * @return The distance.
     */
    public int distanceTo(Node node){
        int a = x - node.getX();
        int b = y - node.getY();

        double calculated = Math.sqrt((a * a + b * b) / 10);
        int result = (int) Math.round(calculated);

        return (result < calculated)? result + 1 : result;
    }

    @Override
    public int compareTo(Node o) {
        return getName().compareTo(o.getName());
    }
}
