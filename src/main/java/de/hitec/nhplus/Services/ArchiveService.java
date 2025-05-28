package de.hitec.nhplus.Services;

import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;

/**
 * Service for managing archive-related operations on patients, caregivers, and treatments in the database.
 * Utilizes the singleton pattern to ensure only one instance of the service.
 */
public class ArchiveService {
    private final PatientDao patientDao;
    private final TreatmentDao treatmentDao;
    private final CaregiverDao caregiverDao;

    /**
     * Returns the singleton instance of ArchiveService.
     * <p>
     *     This constructor is only used for testing purposes and a workaround for the mockito issue.
     * </p>
     */
    public ArchiveService(PatientDao patientDao, TreatmentDao treatmentDao, CaregiverDao caregiverDao) {
        this.patientDao = patientDao;
        this.treatmentDao = treatmentDao;
        this.caregiverDao = caregiverDao;
    }

    /**
     * Returns the singleton instance of ArchiveService.
     * <p>
     *     This constructor is the default one and should be used in production.
     * </p>
     */
    public ArchiveService() {
        this.patientDao = DaoFactory.getDaoFactory().createPatientDAO();
        this.treatmentDao = DaoFactory.getDaoFactory().createTreatmentDao();
        this.caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
    }

    private static final class ArchiveServiceHolder {
        private static final ArchiveService instance = new ArchiveService();
    }

    /**
     * Returns the singleton instance of ArchiveService.
     *
     * @return the singleton instance of ArchiveService
     */
    public static ArchiveService getInstance() {
        return ArchiveServiceHolder.instance;
    }

    /**
     * Deletes archived patients that have no associated, undeletable treatments.
     *
     * @return the number of deleted patients
     */
    public int deleteOldPatients() {
        var patients = patientDao.getAllArchived();
        var treatments = treatmentDao.getAll();
        int count = 0;

        for (Patient patient : patients) {
            if (patient.canBeDeleted() &&
                    treatments.stream().noneMatch(treatment -> treatment.getPid() == patient.getId() && !treatment.canBeDeleted())) {
                patientDao.delete(patient.getId());
                count++;
            }
        }

        return count;
    }

    /**
     * Deletes archived caregivers that have no associated, undeletable treatments.
     *
     * @return the number of deleted caregivers
     */
    public int deleteOldCaregivers() {
        var caregivers = caregiverDao.getAllArchived();
        var treatments = treatmentDao.getAll();
        int count = 0;

        for (Caregiver caregiver : caregivers) {
            if (caregiver.canBeDeleted()) {
                boolean b = true;
                for (Treatment treatment : treatments) {
                    if (treatment.getCid() == caregiver.getId() && !treatment.canBeDeleted()) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    caregiverDao.delete(caregiver.getId());
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Deletes all treatments that are older than 10 years.
     * @return the number of deleted treatments.
     */
    public int deleteOldTreatments() {
        var treatments = treatmentDao.getAll();
        var count = 0;

        for (Treatment treatment : treatments) {
            if (treatment.canBeDeleted()) {
                treatmentDao.delete(treatment.getId());
                count++;
            }
        }

        return count;
    }
}
