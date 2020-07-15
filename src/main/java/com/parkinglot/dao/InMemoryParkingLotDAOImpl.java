package com.parkinglot.dao;

import com.parkinglot.model.Vehicle;
import com.parkinglot.model.strategy.NearestFirstParkingStrategy;
import com.parkinglot.model.strategy.ParkingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vinod Kandula
 */
public class InMemoryParkingLotDAOImpl<T extends Vehicle> implements ParkingLotDAO<T> {

    private Map<Integer, ParkingLotLevelDAO<T>> levelParkingMap;

    @SuppressWarnings("rawtypes")
    private static InMemoryParkingLotDAOImpl instance = null;

    @SuppressWarnings("unchecked")
    public static <T extends Vehicle> InMemoryParkingLotDAOImpl<T> getInstance(int parkingLevels, int capacity) {
        if (instance == null) {
            synchronized (InMemoryParkingLotDAOImpl.class) {
                if (instance == null) {
                    instance = new InMemoryParkingLotDAOImpl<T>(parkingLevels, capacity);
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Vehicle> InMemoryParkingLotDAOImpl<T> getInstance(int parkingLevels, Map<Integer, Integer> capacityMap, List<ParkingStrategy> parkingStrategies) {
        // Make sure the each of the lists are of equal size
        if (instance == null) {
            synchronized (InMemoryParkingLotDAOImpl.class) {
                if (instance == null) {
                    instance = new InMemoryParkingLotDAOImpl<T>(parkingLevels, capacityMap, parkingStrategies);
                }
            }
        }
        return instance;
    }

    private InMemoryParkingLotDAOImpl(int parkingLevels, int capacity) {
        if (levelParkingMap == null)
            levelParkingMap = new HashMap<>();
        for (int i = 1; i <= parkingLevels; i++) {
            levelParkingMap.put(i, InMemoryParkingLotLevelDAOImpl.getInstance(i, capacity, new NearestFirstParkingStrategy()));
        }
    }

    private InMemoryParkingLotDAOImpl(int parkingLevels, Map<Integer, Integer> capacityList, List<ParkingStrategy> parkingStrategies) {
        if (levelParkingMap == null)
            levelParkingMap = new HashMap<>();
        for (int i = 1; i <= parkingLevels; i++) {
            levelParkingMap.put(i, InMemoryParkingLotLevelDAOImpl.getInstance(i, capacityList.get(i), new NearestFirstParkingStrategy()));
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
    public void cleanup() {
        for (ParkingLotLevelDAO<T> levelDataManager : levelParkingMap.values()) {
            levelDataManager.cleanUp();
        }
        levelParkingMap = null;
        instance = null;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
