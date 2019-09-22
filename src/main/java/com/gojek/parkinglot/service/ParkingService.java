package com.gojek.parkinglot.service;

import com.gojek.parkinglot.exception.ParkingException;
import com.gojek.parkinglot.model.Vehicle;

import java.util.Optional;

/**
 * @author Vinod Kandula
 */
public interface ParkingService extends Service {

    public void createParkingLot(int level, int capacity) throws ParkingException;

    public Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException;

    public boolean unPark(int level, int slotNumber) throws ParkingException;

    public void getStatus(int level) throws ParkingException;

    public Optional<Integer> getAvailableSlotsCount(int level) throws ParkingException;

    public void getRegNumberForColor(int level, String color) throws ParkingException;

    public void getSlotNumbersFromColor(int level, String color) throws ParkingException;

    public int getSlotNoFromRegistrationNo(int level, String registrationNo) throws ParkingException;

    public int getCapacity(int level) throws ParkingException;

    public void doCleanup();

}
