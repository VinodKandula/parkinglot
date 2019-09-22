package com.gojek.parkinglot.service;

import com.gojek.parkinglot.constants.Constants;
import com.gojek.parkinglot.dao.InMemoryParkingDataManagerImpl;
import com.gojek.parkinglot.dao.ParkingDataManager;
import com.gojek.parkinglot.exception.ErrorCode;
import com.gojek.parkinglot.exception.ParkingException;
import com.gojek.parkinglot.model.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Vinod Kandula
 */
public class ParkingServiceImpl implements ParkingService {

    private ParkingDataManager<Vehicle> dataManager = null;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void createParkingLot(int level, int capacity) throws ParkingException {
        if (dataManager != null)
            throw new ParkingException(ErrorCode.PARKING_ALREADY_EXIST.getMessage());

        this.dataManager = InMemoryParkingDataManagerImpl.getInstance(level, capacity);
        System.out.println("Created parking lot with " + capacity + " slots");
    }

    @Override
    public Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException {
        validateParkingLot();

        Optional<Integer> value = Optional.empty();
        lock.writeLock().lock();

        try {
            value = Optional.of(dataManager.park(level, vehicle));
            if (value.get() == Constants.NOT_AVAILABLE)
                System.out.println("Sorry, parking lot is full");
            else if (value.get() == Constants.VEHICLE_ALREADY_EXIST)
                System.out.println("Sorry, vehicle is already parked.");
            else
                System.out.println("Allocated slot number: " + value.get());
        }
        catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        }
        finally {
            lock.writeLock().unlock();
        }
        return value;
    }

    private void validateParkingLot() throws ParkingException{
        if (dataManager == null) {
            throw new ParkingException(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage());
        }
    }

    @Override
    public boolean unPark(int level, int slotNumber) throws ParkingException {
        validateParkingLot();
        lock.writeLock().lock();
        try {
            if (dataManager.unPark(level, slotNumber)) {
                System.out.println("Slot number " + slotNumber + " is free");
                return true;
            } else
                System.out.println("Slot number is Empty Already.");
        }
        catch (Exception e) {
            throw new ParkingException(ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"), e);
        }
        finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    @Override
    public void getStatus(int level) throws ParkingException {
        validateParkingLot();
        lock.readLock().lock();
        try {
            System.out.println("Slot No.\tRegistration No.\tColor");
            List<String> statusList = dataManager.getStatus(level);
            if (statusList.size() == 0)
                System.out.println("Sorry, parking lot is empty.");
            else {
                for (String statusSting : statusList) {
                    System.out.println(statusSting);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Integer> getAvailableSlotsCount(int level) throws ParkingException {
        validateParkingLot();
        Optional<Integer> value = Optional.empty();
        lock.readLock().lock();
        try {
            value = Optional.of(dataManager.getAvailableSlotsCount(level));
        }
        catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        }
        finally {
            lock.readLock().unlock();
        }
        return value;
    }

    @Override
    public void getRegNumberForColor(int level, String color) throws ParkingException {
        validateParkingLot();
        lock.readLock().lock();
        try {
            List<String> registrationList = dataManager.getRegNumberForColor(level, color);
            if (registrationList.size() == 0)
                System.out.println("Not Found");
            else
                System.out.println(String.join(",", registrationList));
        }
        catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void getSlotNumbersFromColor(int level, String color) throws ParkingException {
        validateParkingLot();
        lock.readLock().lock();
        try {
            List<Integer> slotList = dataManager.getSlotNumbersFromColor(level, color);
            if (slotList.size() == 0)
                System.out.println("Not Found");
            StringJoiner joiner = new StringJoiner(",");
            for (Integer slot : slotList) {
                joiner.add(slot + "");
            }
            System.out.println(joiner.toString());
        }
        catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getSlotNoFromRegistrationNo(int level, String registrationNo) throws ParkingException {
        int value = -1;
        validateParkingLot();
        lock.readLock().lock();
        try {
            value = dataManager.getSlotNoFromRegistrationNo(level, registrationNo);
            System.out.println(value != -1 ? value : "Not Found");
        }
        catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        }
        finally {
            lock.readLock().unlock();
        }
        return value;
    }

    @Override
    public int getCapacity(int level) throws ParkingException {
        validateParkingLot();
        int capacity;
        try {
            capacity = dataManager.getCapacity(level);
            System.out.println(capacity > 0 ? capacity : "Not Found");
        }
        catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        }
        return capacity;
    }

    @Override
    public void cleanup() {
        if (dataManager != null)
            dataManager.doCleanup();
    }
}
