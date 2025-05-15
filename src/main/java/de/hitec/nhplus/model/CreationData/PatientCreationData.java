package de.hitec.nhplus.model.CreationData;

import java.time.LocalDate;

/**
 * Data class to create a new patient.
 */
public record PatientCreationData(String firstName, String surname, LocalDate dateOfBirth, String careLevel, String roomNumber, String assets) {
}
