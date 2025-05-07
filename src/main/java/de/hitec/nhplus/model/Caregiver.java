package de.hitec.nhplus.model;

public class Caregiver extends Person {
    private final String ID;
    private String phoneNumber;

    public Caregiver(String firstName, String lastName, String ID, String phoneNumber) {
        super(firstName, lastName);
        this.ID = ID;
        this.phoneNumber = phoneNumber;
    }

    public String getID() {
        return ID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
