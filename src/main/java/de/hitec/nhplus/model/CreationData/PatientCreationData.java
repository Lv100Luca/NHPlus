package de.hitec.nhplus.model.CreationData;

import java.time.LocalDate;

/**
 * Data class for creating a new patient.
 *
 * @param firstName first name of the patient
 * @param surname surname of the patient
 * @param dateOfBirth date of birth of the patient
 * @param careLevel care level of the patient
 * @param roomNumber room number of the patient
 * @param archivedOn date when the patient was archived
 */
public record PatientCreationData(String firstName, String surname, LocalDate dateOfBirth, String careLevel, String roomNumber, LocalDate archivedOn) {
}
