package com.gojek.parkinglot.dao;

import com.gojek.parkinglot.constants.Constants;
import com.gojek.parkinglot.model.Vehicle;
import com.gojek.parkinglot.model.strategy.NearestFirstParkingStrategy;
import com.gojek.parkinglot.model.strategy.ParkingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Vinod Kandula
 */
public class InMemoryParkingLevelDataManagerImpl<T extends Vehicle> implements ParkingLevelDataManager<T> {

    // For Multilevel Parking lot - 0 -> Ground floor 1 -> First Floor etc
    private AtomicInteger level			    = new AtomicInteger(0);
    private AtomicInteger	capacity		= new AtomicInteger();
    private AtomicInteger	availability	= new AtomicInteger();
    // Allocation Strategy for parking
    private ParkingStrategy parkingStrategy;
    // this is per level - slot - vehicle
    private Map<Integer, Optional<T>> slotVehicleMap;
    private Map<String, Integer> vehicleSlotMap;

    @SuppressWarnings("rawtypes")
    private static InMemoryParkingLevelDataManagerImpl instance = null;

    @SuppressWarnings("unchecked")
    public static <T extends Vehicle> InMemoryParkingLevelDataManagerImpl<T> getInstance(int level, int capacity,
                                                                               ParkingStrategy parkingStrategy) {
        if (instance == null) {
            synchronized (InMemoryParkingLevelDataManagerImpl.class) {
                if (instance == null) {
                    instance = new InMemoryParkingLevelDataManagerImpl(level, capacity, parkingStrategy);
                }
            }
        }
        return instance;
    }

    private InMemoryParkingLevelDataManagerImpl(int level, int capacity, ParkingStrategy parkingStrategy) {
        this.level.set(level);
        this.capacity.set(capacity);
        this.availability.set(capacity);
        this.parkingStrategy = parkingStrategy;
        if (parkingStrategy == null)
            parkingStrategy = new NearestFirstParkingStrategy();

        slotVehicleMap = new ConcurrentHashMap<>();
        vehicleSlotMap = new ConcurrentHashMap<>();

        for (int i = 1; i <= capacity; i++) {
            slotVehicleMap.put(i, Optional.empty());
            parkingStrategy.add(i);
        }
    }

    @Override
    public int park(T vehicle) {
        int availableSlot;
        if (availability.get() == 0) {
            return Constants.NOT_AVAILABLE;
        }
        else {
            if (vehicleSlotMap.get(vehicle.getRegistrationNumber()) != null && vehicleSlotMap.get(vehicle.getRegistrationNumber()) > 0 )
                return Constants.VEHICLE_ALREADY_EXIST;

            availableSlot = parkingStrategy.getSlot();
            slotVehicleMap.put(availableSlot, Optional.of(vehicle));
            vehicleSlotMap.put(vehicle.getRegistrationNumber(), availableSlot);
            availability.decrementAndGet();
            parkingStrategy.removeSlot(availableSlot);
        }
        return availableSlot;
    }

    @Override
    public boolean unPark(int slotNumber) {
        if (!slotVehicleMap.get(slotNumber).isPresent()) // Slot already empty
            return false;
        availability.incrementAndGet();
        parkingStrategy.add(slotNumber);
        vehicleSlotMap.remove((slotVehicleMap.get(slotNumber)).get().getRegistrationNumber());
        slotVehicleMap.put(slotNumber, Optional.empty());
        return true;
    }

    @Override
    public List<String> getStatus() {
        List<String> statusList = new ArrayList<>();
        for (int i = 1; i <= capacity.get(); i++) {
            Optional<T> vehicle = slotVehicleMap.get(i);
            if (vehicle.isPresent()) {
                statusList.add(i + "\t\t" + vehicle.get().getRegistrationNumber() + "\t\t" + vehicle.get().getColor());
            }
        }
        return statusList;
    }

    @Override
    public List<String> getRegNumberForColor(String color) {
        List<String> statusList = new ArrayList<>();
        for (int i = 1; i <= capacity.get(); i++) {
            Optional<T> vehicle = slotVehicleMap.get(i);
            if (vehicle.isPresent() && color.equalsIgnoreCase(vehicle.get().getColor())) {
                statusList.add(vehicle.get().getRegistrationNumber());
            }
        }
        return statusList;
    }

    @Override
    public List<Integer> getSlotNumbersFromColor(String colour) {
        List<Integer> slotList = new ArrayList<>();
        for (int i = 1; i <= capacity.get(); i++) {
            Optional<T> vehicle = slotVehicleMap.get(i);
            if (vehicle.isPresent() && colour.equalsIgnoreCase(vehicle.get().getColor())) {
                slotList.add(i);
            }
        }
        return slotList;
    }

    @Override
    public int getSlotNoFromRegistrationNo(String registrationNo) {
        int result = Constants.NOT_FOUND;
        for (int i = 1; i <= capacity.get(); i++) {
            Optional<T> vehicle = slotVehicleMap.get(i);
            if (vehicle.isPresent() && registrationNo.equalsIgnoreCase(vehicle.get().getRegistrationNumber())) {
                result = i;
            }
        }
        return result;
    }

    @Override
    public int getAvailableSlotsCount() {
        return availability.get();
    }

    @Override
    public int getCapacity() {
        return this.capacity.get();
    }

    @Override
    public void cleanUp() {
        this.level = new AtomicInteger();
        this.capacity = new AtomicInteger();
        this.availability = new AtomicInteger();
        this.parkingStrategy = null;
        slotVehicleMap = null;
        instance = null;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
