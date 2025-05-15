package de.hitec.nhplus.model.CreationData;

import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data class to create a new treatment.
 *
 * @param patientId id of the patient
 * @param date date of the treatment
 * @param begin time of the start of the treatment in format "hh:MM"
 * @param end time of the end of the treatment in format "hh:MM".
 * @param description description of the treatment
 * @param remarks remarks to the treatment
 */
public record TreatmentCreationData (long patientId, LocalDate date, LocalTime begin, LocalTime end, String description, String remarks, long caregiverId) {

    /**
     * Overloaded constructor with patient model.
     *
     * @param patient patient model
     * @param date date of the treatment
     * @param begin time of the start of the treatment in format "hh:MM"
     * @param end time of the end of the treatment in format "hh:MM".
     * @param description description of the treatment
     * @param remarks remarks to the treatment
     */
    public TreatmentCreationData(Patient patient, LocalDate date, LocalTime begin, LocalTime end, String description, String remarks, Caregiver caregiver) {
        this(patient.getId(), date, begin, end, description, remarks, caregiver.getId());
    }
}
