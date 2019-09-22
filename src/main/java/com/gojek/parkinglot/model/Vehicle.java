package com.gojek.parkinglot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author Vinod Kandula
 */
@AllArgsConstructor
@Data
public abstract class Vehicle implements Externalizable {

    private String registrationNumber = null;
    private String	color			= null;


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getRegistrationNumber());
        out.writeObject(getColor());
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setRegistrationNumber((String) in.readObject());
        setColor((String) in.readObject());
    }
}
