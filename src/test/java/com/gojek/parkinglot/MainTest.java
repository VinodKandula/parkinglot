package com.gojek.parkinglot;

import com.gojek.parkinglot.constants.Constants;
import com.gojek.parkinglot.exception.ErrorCode;
import com.gojek.parkinglot.exception.ParkingException;
import com.gojek.parkinglot.model.Car;
import com.gojek.parkinglot.service.ParkingService;
import com.gojek.parkinglot.service.ParkingServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * @author Vinod Kandula
 */
public class MainTest {

    private int							parkingLevel;
    private final ByteArrayOutputStream outContent	= new ByteArrayOutputStream();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ParkingService parkingService = null;

    @Before
    public void init() {
        parkingLevel = 10;
    }

    @After
    public void cleanUp() {
        if (parkingService != null)
            parkingService.doCleanup();
    }

    @Test
    public void whenCreatingParkingLot_shouldMatchCapacity() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 77);
        assertEquals(77, parkingService.getCapacity(parkingLevel));
    }

    @Test
    public void whenCreatingAlreadyExistParkingLot_shouldThrowException() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 7);
        assertEquals(7, parkingService.getCapacity(parkingLevel));
        thrown.expect(ParkingException.class);
        thrown.expectMessage(is(ErrorCode.PARKING_ALREADY_EXIST.getMessage()));
        parkingService.createParkingLot(parkingLevel, 65);
    }

    @Test
    public void whenParkVehiclesOnEmptyParkingLot_shouldThrowParkingLotNotExist() throws Exception {
        parkingService = new ParkingServiceImpl();
        thrown.expect(ParkingException.class);
        thrown.expectMessage(is(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage()));
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        //assertEquals("Sorry,CarParkingDoesnotExist", outContent.toString().trim().replace(" ", ""));
    }

    @Test
    public void whenParkVehicles_shouldMatchAvailableSlots() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 11);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
        assertEquals(8, parkingService.getAvailableSlotsCount(parkingLevel).get().intValue());
    }

    @Test
    public void whenEmptyParkingLot_shouldMatchWithSlotsAvailable() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 6);
        assertEquals(6, parkingService.getAvailableSlotsCount(parkingLevel).get().intValue());
    }

    
}