package com.gojek.parkinglot.model.strategy;

/**
 * @author Vinod Kandula
 */
public interface ParkingStrategy {

    public void add(int i);

    public int getSlot();

    public void removeSlot(int slot);

}
