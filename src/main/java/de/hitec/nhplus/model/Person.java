package de.hitec.nhplus.model;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public abstract class Person implements Entity, Archivable {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty surname;
    private LocalDate archivedOn;

    public Person(String firstName, String surname, LocalDate archivedOn) {
        this.firstName = new SimpleStringProperty(firstName);
        this.surname = new SimpleStringProperty(surname);
        this.archivedOn = archivedOn;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getSurname() {
        return surname.get();
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getFullName() {
        return this.getSurname() + " " + this.getFirstName();
    }

    @Override
    public boolean isArchived() {
        return archivedOn != null;
    }

    @Override
    public boolean canBeDeleted() {
        return archivedOn.isBefore(LocalDate.now().minusYears(10));
    }
}
