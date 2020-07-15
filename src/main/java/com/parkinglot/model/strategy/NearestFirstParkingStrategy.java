package com.parkinglot.model.strategy;

import java.util.TreeSet;

/**
 * @author Vinod Kandula
 */
public class NearestFirstParkingStrategy implements ParkingStrategy {
    private TreeSet<Integer> freeSlots;

    public NearestFirstParkingStrategy() {
        freeSlots = new TreeSet<Integer>();
    }

    @Override
    public void add(int i) {
        freeSlots.add(i);
    }

    @Override
    public int getSlot() {
        return freeSlots.first();
    }

    @Override
    public void removeSlot(int slot) {
        freeSlots.remove(slot);
    }
}
