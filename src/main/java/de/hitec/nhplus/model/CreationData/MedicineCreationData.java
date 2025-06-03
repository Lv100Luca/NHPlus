package de.hitec.nhplus.model.CreationData;

/**
 * Data class for creating a new medicine.
 *
 * @param name name of the medicine
 * @param storage storage of the medicine
 * @param expirationDate expiration date of the medicine
 */
public record MedicineCreationData(String name, String storage, java.time.LocalDate expirationDate) {
}
