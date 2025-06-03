package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Medicines are used by the patient to treat their symptoms.
 */
public class Medicine implements Entity {
    private final long id;
    private final String name;
    private final String storage;
    private final LocalDate expirationDate;

    /**
     * Constructor to initiate an object of class <code>Medicine</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a medicine id (mid).
     *
     * @param id            Medicine id.
     * @param name           Name of the medicine.
     * @param storage        Storage of the medicine.
     * @param expirationDate Expiration date of the medicine.
     */
    private Medicine(long id, String name, String storage, String expirationDate) {
        this.id = id;
        this.name = name;
        this.storage = storage;
        this.expirationDate = DateConverter.convertStringToLocalDate(expirationDate);
    }

    /**
     * Creates a new medicine from a result set.
     * Ensures that entities can only be created from result sets that came from the database.
     *
     * @param result The result set to create the medicine from.
     * @return The medicine created from the result set.
     * @throws SQLException If the result set is empty.
     */
    public static Medicine fromResultSet(ResultSet result) throws SQLException {
        long id = result.getLong(1);
        String name = result.getString(2);
        String storage = result.getString(3);
        String expirationDate = result.getString(4);

        return new Medicine(id, name, storage, expirationDate);
    }

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStorage() {
        return storage;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}