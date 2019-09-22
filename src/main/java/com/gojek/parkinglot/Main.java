package com.gojek.parkinglot;

import com.gojek.parkinglot.cmd.CmdRequestProcessor;
import com.gojek.parkinglot.cmd.CmdRequestProcessorImpl;
import com.gojek.parkinglot.exception.ErrorCode;
import com.gojek.parkinglot.exception.ParkingException;
import com.gojek.parkinglot.service.ParkingServiceImpl;

import java.io.*;


/**
 * @author Vinod Kandula
 */
public class Main {

    public static void main(String[] args) {
        CmdRequestProcessor cmdRequestProcessor = new CmdRequestProcessorImpl();
        cmdRequestProcessor.setService(new ParkingServiceImpl());
        BufferedReader bufferReader = null;
        String input = null;

        try {
            /*System.out.println("\n\n\n\n\n");
            System.out.println("===================================================================");
            System.out.println("===================      GOJEK PARKING LOT     ====================");
            System.out.println("===================================================================");
            printUsage();*/
            switch (args.length) {
                case 0: {
                    // Interactive: command-line input/output
                    System.out.println("Please Enter 'exit' to end Execution");
                    System.out.println("Input:");
                    while (true) {
                        try {
                            bufferReader = new BufferedReader(new InputStreamReader(System.in));
                            input = bufferReader.readLine().trim();
                            if (input.equalsIgnoreCase("exit")) {
                                break;
                            }
                            else {
                                if (cmdRequestProcessor.validate(input)) {
                                    try {
                                        cmdRequestProcessor.execute(input.trim());
                                    }
                                    catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                                else {
                                    //printUsage();
                                }
                            }
                        }
                        catch (Exception e) {
                            throw new ParkingException(ErrorCode.INVALID_REQUEST.getMessage(), e);
                        }
                    }
                    break;
                }
                case 1: {
                    // File input/output
                    File inputFile = new File(args[0]);
                    try {
                        bufferReader = new BufferedReader(new FileReader(inputFile));
                        int lineNo = 1;
                        while ((input = bufferReader.readLine()) != null) {
                            input = input.trim();
                            if (cmdRequestProcessor.validate(input)) {
                                try {
                                    cmdRequestProcessor.execute(input);
                                }
                                catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            else
                                System.out.println("Incorrect Command Found at line: " + lineNo + " ,Input: " + input);
                            lineNo++;
                        }
                    }
                    catch (Exception e) {
                        throw new ParkingException(ErrorCode.INVALID_FILE.getMessage(), e);
                    }
                    break;
                }
                default:
                    System.out.println("Invalid input. Usage Style: java -jar <jar_file_path> <input_file_path>");
            }
        }
        catch (ParkingException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        finally {
            try {
                if (bufferReader != null)
                    bufferReader.close();
            }
            catch (IOException e) {
            }
        }
    }

    private static void printUsage() {
        StringBuilder buffer = new StringBuilder();
        buffer = buffer.append(
                "--------------Please Enter one of the below commands. {variable} to be replaced -----------------------")
                .append("\n");
        buffer = buffer.append("A) For creating parking lot of size n               ---> create_parking_lot {capacity}")
                .append("\n");
        buffer = buffer
                .append("B) To park a car                                    ---> park <<car_number>> {car_clour}")
                .append("\n");
        buffer = buffer.append("C) Remove(Unpark) car from parking                  ---> leave {slot_number}")
                .append("\n");
        buffer = buffer.append("D) Print status of parking slot                     ---> status").append("\n");
        buffer = buffer.append(
                "E) Get cars registration no for the given car color ---> registration_numbers_for_cars_with_colour {car_color}")
                .append("\n");
        buffer = buffer.append(
                "F) Get slot numbers for the given car color         ---> slot_numbers_for_cars_with_colour {car_color}")
                .append("\n");
        buffer = buffer.append(
                "G) Get slot number for the given car number         ---> slot_number_for_registration_number {car_number}")
                .append("\n");
        System.out.println(buffer.toString());
    }


}
