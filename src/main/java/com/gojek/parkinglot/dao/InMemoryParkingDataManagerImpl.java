package com.gojek.parkinglot.dao;

import com.gojek.parkinglot.model.Vehicle;
import com.gojek.parkinglot.model.strategy.NearestFirstParkingStrategy;
import com.gojek.parkinglot.model.strategy.ParkingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vinod Kandula
 */
public class InMemoryParkingDataManagerImpl<T extends Vehicle> implements ParkingDataManager<T> {

    private Map<Integer, ParkingLevelDataManager<T>> levelParkingMap;

    @SuppressWarnings("rawtypes")
    private static InMemoryParkingDataManagerImpl instance = null;

    @SuppressWarnings("unchecked")
    public static <T extends Vehicle> InMemoryParkingDataManagerImpl<T> getInstance(int parkingLevels, int capacity) {
        if (instance == null) {
            synchronized (InMemoryParkingDataManagerImpl.class) {
                if (instance == null) {
                    instance = new InMemoryParkingDataManagerImpl<T>(parkingLevels, capacity);
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Vehicle> InMemoryParkingDataManagerImpl<T> getInstance(int parkingLevels, Map<Integer, Integer> capacityMap, List<ParkingStrategy> parkingStrategies) {
        // Make sure the each of the lists are of equal size
        if (instance == null) {
            synchronized (InMemoryParkingDataManagerImpl.class) {
                if (instance == null) {
                    instance = new InMemoryParkingDataManagerImpl<T>(parkingLevels, capacityMap, parkingStrategies);
                }
            }
        }
        return instance;
    }

    private InMemoryParkingDataManagerImpl(int parkingLevels, int capacity) {
        if (levelParkingMap == null)
            levelParkingMap = new HashMap<>();
        for (int i = 1; i <= parkingLevels; i++) {
            levelParkingMap.put(i, InMemoryParkingLevelDataManagerImpl.getInstance(i, capacity, new NearestFirstParkingStrategy()));
        }
    }

    private InMemoryParkingDataManagerImpl(int parkingLevels, Map<Integer, Integer> capacityList, List<ParkingStrategy> parkingStrategies) {
        if (levelParkingMap == null)
            levelParkingMap = new HashMap<>();
        for (int i = 1; i <= parkingLevels; i++) {
            levelParkingMap.put(i, InMemoryParkingLevelDataManagerImpl.getInstance(i, capacityList.get(i), new NearestFirstParkingStrategy()));
        }
    }

    @Override
    public int park(int level, T vehicle) {
        return levelParkingMap.get(level).park(vehicle);
    }

    @Override
    public boolean unPark(int level, int slotNumber) {
        return levelParkingMap.get(level).unPark(slotNumber);
    }

    @Override
    public List<String> getStatus(int level) {
        return levelParkingMap.get(level).getStatus();
    }

    @Override
    public List<String> getRegNumberForColor(int level, String color) {
        return levelParkingMap.get(level).getRegNumberForColor(color);
    }

    @Override
    public List<Integer> getSlotNumbersFromColor(int level, String color) {
        return levelParkingMap.get(level).getSlotNumbersFromColor(color);
    }

    @Override
    public int getSlotNoFromRegistrationNo(int level, String registrationNo) {
        return levelParkingMap.get(level).getSlotNoFromRegistrationNo(registrationNo);
    }

    @Override
    public int getAvailableSlotsCount(int level) {
        return levelParkingMap.get(level).getAvailableSlotsCount();
    }

    @Override
    public int getCapacity(int level) {
        return this.levelParkingMap.get(level).getCapacity();
    }

    @Override
    public void doCleanup() {
        for (ParkingLevelDataManager<T> levelDataManager : levelParkingMap.values()) {
            levelDataManager.doCleanUp();
        }
        levelParkingMap = null;
        instance = null;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
