package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Caregiver extends Person {
    private SimpleLongProperty cid;
    private final SimpleStringProperty phoneNumber;

    public Caregiver(String firstName, String surname, String phoneNumber) {
        super(firstName, surname);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public Caregiver(long cid, String firstName, String surname, String phoneNumber) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public long getCid() {
        return cid.get();
    }

    public SimpleLongProperty cidProperty() {
        return cid;
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
