package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Medicines are used by the patient to treat their symptoms.
 */
public class Medicine {
    private SimpleLongProperty mid;
    private final SimpleStringProperty name;
    private final SimpleStringProperty storage;
    private final SimpleStringProperty expirationDate;

    /**
     * Constructor to initiate an object of class <code>Medicine</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a medicine id (mid).
     *
     * @param name           Name of the medicine.
     * @param storage        Storage of the medicine.
     * @param expirationDate Expiration date of the medicine.
     */
    public Medicine(String name, String storage, String expirationDate) {
        this.name = new SimpleStringProperty(name);
        this.storage = new SimpleStringProperty(storage);
        this.expirationDate = new SimpleStringProperty(expirationDate);
    }

    /**
     * Constructor to initiate an object of class <code>Medicine</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a medicine id (mid).
     *
     * @param mid            Medicine id.
     * @param name           Name of the medicine.
     * @param storage        Storage of the medicine.
     * @param expirationDate Expiration date of the medicine.
     */
    public Medicine(long mid, String name, String storage, String expirationDate) {
        this.mid = new SimpleLongProperty(mid);
        this.name = new SimpleStringProperty(name);
        this.storage = new SimpleStringProperty(storage);
        this.expirationDate = new SimpleStringProperty(expirationDate);
    }

    public long getMid() {
        return mid.get();
    }

    public SimpleLongProperty midProperty() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid.set(mid);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getStorage() {
        return storage.get();
    }

    public SimpleStringProperty storageProperty() {
        return storage;
    }

    public String getExpirationDate() {
        return expirationDate.get();
    }

    public SimpleStringProperty expirationDateProperty() {
        return expirationDate;
    }

    public String toString() {
        return this.name.toString();
    }
}
