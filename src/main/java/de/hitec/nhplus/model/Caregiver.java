package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Class for a caregiver.
 */
public class Caregiver extends Person {
    private SimpleLongProperty id;
    private final SimpleStringProperty phoneNumber;

    /**
     * Private constructor to create a new caregiver from the <code>fromResultSet</code> method.
     *
     * @param id          The id of the caregiver.
     * @param firstName   The first name of the caregiver.
     * @param surname     The surname of the caregiver.
     * @param phoneNumber The phone number of the caregiver.
     * @param archivedOn  The date when the caregiver was archived.
     */
    private Caregiver(long id, String firstName, String surname, String phoneNumber, LocalDate archivedOn) {
        super(firstName, surname, archivedOn);
        this.id = new SimpleLongProperty(id);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    /**
     * Creates a new caregiver from a result set.
     * Ensures that entities can only be created from result sets that came from the database.
     *
     * @param result The result set to create the caregiver from.
     * @return The caregiver created from the result set.
     * @throws SQLException If the result set is empty.
     */
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
