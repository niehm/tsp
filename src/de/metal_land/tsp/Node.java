package de.metal_land.tsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

/**
 * @author nieh
 */
@Data
@Log
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private String name;
    private int x;
    private int y;

    public int distanceTo(Node node){
        int a = x - node.getX();
        int b = y - node.getY();

        double calculated = Math.sqrt((a*a + b*b)/10);
        int result = (int) Math.round(calculated );

        return (result < calculated)? result+1 : result;
    }
}
