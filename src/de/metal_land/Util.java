package de.metal_land;

import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Some useful Utilities.
 * @author nieh
 */
@Log
public class Util {

    /**
     * Returns a reversed version of the given List.
     * @param list The list to Reverse.
     * @param <T> The generic Type of the List.
     * @return The reversed List.
     */
    public static <T> List<T> reverseList(List<T> list){
        ListIterator<T> it = list.listIterator(list.size());
        List<T> newList = new ArrayList<>(list.size());
        while (it.hasPrevious()){
            newList.add(it.previous());
        }

        return newList;
    }

    /**
     * Returns a list from the index specified by the parameter from inclusive to the parameter to exclusive.
     * @param list The list to extract the sublist from.
     * @param from The index of the beginning of the sublist inclusive.
     * @param to The index of the end of the sublist exclusive.
     * @param <T> The generic Type of the List.
     * @return A the sublist as new List.
     */
    public static <T> List<T> sublist(List<T> list, int from, int to){
        List<T> newList = new ArrayList<>(to - from);
        for(int i=from; i<to; i++){
            newList.add(list.get(i));
        }

        return newList;
    }
}
