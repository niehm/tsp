package de.metal_land.tsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 12/18/13
 *
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

    public double distanceTo(Node node){
        int a = (x > node.getX())? x - node.getX() : node.getX() - x;
        int b = (y > node.getY())? y - node.getY() : node.getY() - y;

        return Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0));
    }
}
