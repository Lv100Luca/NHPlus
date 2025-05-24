package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Caregiver extends Person {
    private SimpleLongProperty id;
    private final SimpleStringProperty phoneNumber;

    private Caregiver(long id, String firstName, String surname, String phoneNumber, LocalDate archivedOn) {
        super(firstName, surname, archivedOn);
        this.id = new SimpleLongProperty(id);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public static Caregiver fromResultSet(ResultSet result) throws SQLException {
        var archivedOn = result.getString(5) == null ? null : DateConverter.convertStringToLocalDate(result.getString(5));

        return new Caregiver(result.getLong(1), result.getString(2),
                result.getString(3), result.getString(4), archivedOn);
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
