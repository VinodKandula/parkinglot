package com.parkinglot;

import com.parkinglot.constants.Constants;
import com.parkinglot.exception.ErrorCode;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.model.Car;
import com.parkinglot.service.ParkingService;
import com.parkinglot.service.ParkingServiceImpl;
import org.hamcrest.CoreMatchers;
import org.junit.*;
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
            parkingService.cleanup();
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
        thrown.expectMessage(CoreMatchers.is(ErrorCode.PARKING_ALREADY_EXIST.getMessage()));
        parkingService.createParkingLot(parkingLevel, 65);
    }

    @Test
    public void whenParkVehiclesOnEmptyParkingLot_shouldThrowParkingLotNotExist() throws Exception {
        parkingService = new ParkingServiceImpl();
        thrown.expect(ParkingException.class);
        thrown.expectMessage(is(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage()));
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
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

    @Test
    public void whenParkingVehiclesOnParkingLotIsFull_shouldMatchWithParkingLotNotAvailable() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 2);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        Optional<Integer> result = parkingService.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
        Assert.assertEquals(Constants.NOT_AVAILABLE, result.get().intValue());
    }

    @Test
    public void whenParkingVehicles_shouldMatchWithNearestSlotAllotment() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 5);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        int slot = parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
        assertEquals(1, slot);
        slot = parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-9999");
        assertEquals(2, slot);
    }

    @Test
    public void whenUnParkingVehicles_shouldMatchResult() throws Exception {
        parkingService = new ParkingServiceImpl();
        thrown.expect(ParkingException.class);
        thrown.expectMessage(is(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage()));
        parkingService.unPark(parkingLevel, 2);

        parkingService.createParkingLot(parkingLevel, 6);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));

        boolean success = parkingService.unPark(parkingLevel, 4);
        assertFalse(success);

        success = parkingService.unPark(parkingLevel, 1);
        assertTrue(success);
    }

    @Test
    public void whenParkingAlreadyExistingVehicle_shouldMatchVehicleAlreadyExists() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 3);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        Optional<Integer> result = parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Constants.VEHICLE_ALREADY_EXIST, result.get().intValue());
    }

    @Test
    public void whenVehicleAlreadyPicked() throws Exception {
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 99);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        boolean success = parkingService.unPark(parkingLevel, 1);
        assertTrue(success);
        success = parkingService.unPark(parkingLevel, 1);
        assertFalse(success);
    }

    @Test
    public void testStatus() throws Exception {
        PrintStream out = System.out;
        System.setOut(new PrintStream(outContent));
        parkingService = new ParkingServiceImpl();
        parkingService.createParkingLot(parkingLevel, 6);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
        parkingService.getStatus(parkingLevel);
        assertTrue(
                "createdaparkinglotwith6slots\nAllocatedslotnumber:1\nAllocatedslotnumber:2\nAllocatedslotnumber:3\nSlotNo.\tRegistrationNo\tColour\n1\t\tKA-01-HH-1234\t\tWhite\n2\t\tKA-01-HH-9999\t\tWhite\n3\t\tKA-01-BB-0001\t\tBlack"
                        .equalsIgnoreCase(outContent.toString().trim().replace(" ", "")));

        System.setOut(out);
    }

    @Test
    public void testGetSlotsByRegNo() throws Exception {
        PrintStream out = System.out;
        System.setOut(new PrintStream(outContent));
        parkingService = new ParkingServiceImpl();
        thrown.expect(ParkingException.class);
        thrown.expectMessage(is(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage()));
        parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
        assertEquals("Sorry,CarParkingDoesnotExist", outContent.toString().trim().replace(" ", ""));
        parkingService.createParkingLot(parkingLevel, 10);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
        assertEquals("Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith6slots\n" + "\n"
                        + "Allocatedslotnumber:1\n" + "\n" + "Allocatedslotnumber:2\n1",
                outContent.toString().trim().replace(" ", ""));
        parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1235");
        assertEquals("Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith10slots\n" + "\n"
                        + "Allocatedslotnumber:1\n" + "\n" + "Allocatedslotnumber:2\n1\nNotFound",
                outContent.toString().trim().replace(" ", ""));
        System.setOut(out);
    }

    @Test
    public void testGetSlotsByColor() throws Exception {
        PrintStream out = System.out;
        System.setOut(new PrintStream(outContent));
        parkingService = new ParkingServiceImpl();
        thrown.expect(ParkingException.class);
        thrown.expectMessage(is(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage()));
        parkingService.getRegNumberForColor(parkingLevel, "white");
        assertEquals("Sorry,CarParkingDoesnotExist", outContent.toString().trim().replace(" ", ""));
        parkingService.createParkingLot(parkingLevel, 7);
        parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        parkingService.getStatus(parkingLevel);
        parkingService.getRegNumberForColor(parkingLevel, "Cyan");
        assertEquals(
                "Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith7slots\n" + "\n" + "Allocatedslotnumber:1\n"
                        + "\n" + "Allocatedslotnumber:2\nKA-01-HH-1234,KA-01-HH-9999",
                outContent.toString().trim().replace(" ", ""));
        parkingService.getRegNumberForColor(parkingLevel, "Red");
        assertEquals(
                "Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith6slots\n" + "\n" + "Allocatedslotnumber:1\n"
                        + "\n" + "Allocatedslotnumber:2\n" + "KA-01-HH-1234,KA-01-HH-9999,Notfound",
                outContent.toString().trim().replace(" ", ""));
        System.setOut(out);
    }
}