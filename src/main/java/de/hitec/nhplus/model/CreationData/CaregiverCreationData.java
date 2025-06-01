package de.hitec.nhplus.model.CreationData;

import java.time.LocalDate;

/**
 * Data class to create a new caregiver.
 */
public record CaregiverCreationData(String firstName, String surname, String phoneNumber, LocalDate archivedOn) {}