package com.gojek.parkinglot.dao;

import com.gojek.parkinglot.model.Vehicle;

import java.util.List;

/**
 * @author Vinod Kandula
 */
public interface ParkingDataManager<T extends Vehicle> {

    public int park(int level, T vehicle);

    public boolean unPark(int level, int slotNumber);

    public List<String> getStatus(int level);

    public List<String> getRegNumberForColor(int level, String color);

    public List<Integer> getSlotNumbersFromColor(int level, String color);

    public int getSlotNoFromRegistrationNo(int level, String registrationNo);

    public int getAvailableSlotsCount(int level);

    public int getCapacity(int level);

    public void doCleanup();
}
