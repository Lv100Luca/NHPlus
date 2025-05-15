package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Caregiver extends Person {
    private SimpleLongProperty id;
    private final SimpleStringProperty phoneNumber;

    private Caregiver(long id, String firstName, String surname, String phoneNumber) {
        super(firstName, surname);
        this.id = new SimpleLongProperty(id);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public static Caregiver fromResultSet(ResultSet result) throws SQLException {
        return new Caregiver(result.getLong(1), result.getString(2),
                result.getString(3), result.getString(4));
    }

    public long getId() {
        return id.get();
    }

    public SimpleLongProperty cidProperty() {
        return id;
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
