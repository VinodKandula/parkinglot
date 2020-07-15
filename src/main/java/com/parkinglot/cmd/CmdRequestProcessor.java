package com.parkinglot.cmd;

import com.parkinglot.constants.CommandLineInputParameters;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.service.Service;

/**
 * @author Vinod Kandula
 */
public interface CmdRequestProcessor {

    public void setService(Service service);

    public void execute(String action) throws ParkingException;

    public default boolean validate(String inputString) {
        // Split the input string to validate command and input value
        boolean valid = true;
        try {
            String[] inputs = inputString.split(" ");
            int params = CommandLineInputParameters.getCommandsParameterMap().get(inputs[0]);
            switch (inputs.length) {
                case 1:
                    if (params != 0) // e.g status -> inputs = 1
                        valid = false;
                    break;
                case 2:
                    if (params != 1) // create_parking_lot 6 -> inputs = 2
                        valid = false;
                    break;
                case 3:
                    if (params != 2) // park KA-01-P-333 White -> inputs = 3
                        valid = false;
                    break;
                default:
                    valid = false;
            }
        }
        catch (Exception e) {
            System.out.println("Input arguments Validation Failed");
            valid = false;
        }
        return valid;
    }
}
