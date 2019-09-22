package com.gojek.parkinglot.dao;

import com.gojek.parkinglot.model.Vehicle;

import java.util.List;

/**
 * @author Vinod Kandula
 */
public interface ParkingLotLevelDAO<T extends Vehicle> {

    public int park(T vehicle);

    public boolean unPark(int slotNumber);

    public List<String> getStatus();

    public List<String> getRegNumberForColor(String color);

    public List<Integer> getSlotNumbersFromColor(String color);

    public int getSlotNoFromRegistrationNo(String registrationNo);

    public int getAvailableSlotsCount();

    public int getCapacity();

    public void cleanUp();

}
