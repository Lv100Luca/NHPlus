package de.hitec.nhplus.model.CreationData;

import java.time.LocalDate;


/**
 * Data class for creating a new caregiver.
 *
 * @param firstName first name of the caregiver
 * @param surname surname of the caregiver
 * @param phoneNumber phone number of the caregiver
 * @param archivedOn date when the caregiver was archived
 */
public record CaregiverCreationData(String firstName, String surname, String phoneNumber, LocalDate archivedOn) {}