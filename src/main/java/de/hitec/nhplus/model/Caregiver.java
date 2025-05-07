package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class Caregiver extends Person {
    private SimpleLongProperty ID;
    private final SimpleStringProperty phoneNumber;
    private final List<Caregiver> allCaregivers = new ArrayList<>();

    public Caregiver(String firstName, String surname, String phoneNumber) {
        super(firstName, surname);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public Caregiver(long ID, String firstName, String surname, String phoneNumber) {
        super(firstName, surname);
        this.ID = new SimpleLongProperty(ID);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public long getID() {
        return ID.get();
    }

    public SimpleLongProperty IDProperty() {
        return ID;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }
}
