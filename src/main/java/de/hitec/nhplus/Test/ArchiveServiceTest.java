package de.hitec.nhplus.Test;

import de.hitec.nhplus.Services.ArchiveService;
import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.CreationData.CaregiverCreationData;
import de.hitec.nhplus.model.CreationData.PatientCreationData;
import de.hitec.nhplus.model.CreationData.TreatmentCreationData;
import de.hitec.nhplus.utils.SetUpDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalDate;
import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalTime;
import static org.junit.Assert.assertEquals;

/**
 * Test class for the <code>ArchiveService</code>.
 * <p>
 * Testing the <code>ArchiveService</code> is a bit tricky because of the mockito issue.
 */
public class ArchiveServiceTest {

    private PatientDao patientDao;
    private TreatmentDao treatmentDao;
    private CaregiverDao caregiverDao;
    private ArchiveService archiveService;
    private Connection connection;

    /**
     * @throws SQLException if the in-memory database cannot be created.
     *                      <p>
     *                      Sets up the in-memory database, the DAOs and the <code>ArchiveService</code> for the testing environment.
     */
    @Before
    public void setUp() throws SQLException {
        // create in memory database
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        // Initialize DAOs with the in-memory connection
        // workaround since i didnt get mockito to work with `ConnectionBuilder`
        patientDao = new PatientDao(connection);
        treatmentDao = new TreatmentDao(connection);
        caregiverDao = new CaregiverDao(connection);

        // another workaround
        archiveService = new ArchiveService(patientDao, treatmentDao, caregiverDao);

        createSchema();
        insertArchiveTestData();
    }

    private void createSchema() {
        // Create schema and tables
        SetUpDB.setUpTablePatient(connection);
        SetUpDB.setUpTableTreatment(connection);
        SetUpDB.setUpTableCaregiver(connection);
    }

    /**
     * Tests the functionality of the <code>ArchiveService</code>.
     * <p>
     * Verifies that the <code>ArchiveService</code> deletes the correct number of patients, caregivers and treatments.
     */
    @Test
    public void testArchiveFunctionality() {
        final int expectedDeletedPatients = 2;
        final int expectedDeletedCaregivers = 2;
        final int expectedDeletedTreatments = 2;

        final int expectedRemainingPatients = 1;
        final int expectedRemainingCaregivers = 1;
        final int expectedRemainingTreatments = 2;

        var deletedPatients = archiveService.deleteOldPatients();
        var deletedCaregivers = archiveService.deleteOldCaregivers();
        var deletedTreatments = archiveService.deleteOldTreatments();


        assertEquals("Expected " + expectedDeletedPatients + " deleted patients, but got " + deletedPatients, expectedDeletedPatients, deletedPatients);
        assertEquals("Expected " + expectedDeletedCaregivers + " deleted caregivers, but got " + deletedCaregivers, expectedDeletedCaregivers, deletedCaregivers);
        assertEquals("Expected " + expectedDeletedTreatments + " deleted treatments, but got " + deletedTreatments, expectedDeletedTreatments, deletedTreatments);

        var remainingPatients = patientDao.getAll();
        var remainingCaregivers = caregiverDao.getAll();
        var remainingTreatments = treatmentDao.getAll();

        assertEquals("Expected " + expectedRemainingPatients + " remaining patients, but got " + remainingPatients.size(), expectedRemainingPatients, remainingPatients.size());
        assertEquals("Expected " + expectedRemainingCaregivers + " remaining caregivers, but got " + remainingCaregivers.size(), expectedRemainingCaregivers, remainingCaregivers.size());
        assertEquals("Expected " + expectedRemainingTreatments + " remaining treatments, but got " + remainingTreatments.size(), expectedRemainingTreatments, remainingTreatments.size());

        System.out.println("deletedPatients: " + deletedPatients);
        System.out.println("deletedCaregivers: " + deletedCaregivers);
        System.out.println("deletedTreatments: " + deletedTreatments);

        System.out.println(" ---- ");

        System.out.println("remainingPatients: " + remainingPatients.size());
        System.out.println("remainingCaregivers: " + remainingCaregivers.size());
        System.out.println("remainingTreatments: " + remainingTreatments.size());
    }

    /**
     * @throws SQLException Tears down the in-memory database.
     */
    @After
    public void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Inserts test data into the database. The data is used to test the archiving functionality.
     * <p>
     * This is a form of test for the <code>ArchiveService</code>.
     * Clean the database before running this method.
     * <p>
     * We expect the following behavior:
     * <p>
     * test1: deleted, should be deleted
     * test2: stays, should stay
     * test3: deleted, should be deleted
     * test4: deleted, should be deleted
     * test5: stays, should stay
     * test6: deleted, should be deleted
     * <p>
     * Deleted Treatments : 2
     * Stays Treatments : 1
     * Deleted Caregivers : 2
     * Stays Caregivers : 1
     * Deleted Patients : 2
     * Stays Patients : 1
     */
    private void insertArchiveTestData() {
        // this patient is archived for a while and not used in any treatment -> should be deleted
        var test1OldUnusedPatient = patientDao.create(new PatientCreationData("test1", "deleted", convertStringToLocalDate("1945-12-01"), "4", "202", convertStringToLocalDate("2013-06-03")));

        // this patient is archived for a while but used in a treatment thats not archived -> should not be deleted
        var test2ArchivedPatientWithTreatment =
                patientDao.create(new PatientCreationData("test2", "stays", convertStringToLocalDate("1954-08-12"), "5", "010", convertStringToLocalDate("2013-06-03")));
        var test2Treatment = treatmentDao.create(new TreatmentCreationData(test2ArchivedPatientWithTreatment.getId(), convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Test 2 Treatment, should stay", 0, 0, null));

        // this patient is archived for a while and used in a treatment that can also be deleted -> should be deleted
        var test3ArchivedPatientWithTreatment =
                patientDao.create(new PatientCreationData("test3", "deleted", convertStringToLocalDate("1949-04-16"), "3", "002", convertStringToLocalDate("2013-06-03")));
        var test3Treatment = treatmentDao.create(new TreatmentCreationData(test3ArchivedPatientWithTreatment.getId(), convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Test 3 Treatment, should be deleted", 0, 0, convertStringToLocalDate("2013-06-03")));

        //  this caregiver is archived for a while and not used in any treatment -> should be deleted
        var test4OldUnusedCaregiver = caregiverDao.create(new CaregiverCreationData("test4", "deleted", "+49 176 12345678", convertStringToLocalDate("2013-06-03")));

        // this caregiver is archived for a while but used in a treatment thats not archived -> should not be deleted
        var test5ArchivedCaregiverWithTreatment =
                caregiverDao.create(new CaregiverCreationData("test5", "stays", "+49 176 23456789", convertStringToLocalDate("2013-06-03")));
        var test5Treatment = treatmentDao.create(new TreatmentCreationData(test2ArchivedPatientWithTreatment.getId(), convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Test 5 Treatment, should stay", test5ArchivedCaregiverWithTreatment.getId(), 0, null));

        // this caregiver is archived for a while and used in a treatment that can also be deleted -> should be deleted
        var test6ArchivedCaregiverWithTreatment =
                caregiverDao.create(new CaregiverCreationData("test6", "deleted", "+49 176 34567890", convertStringToLocalDate("2013-06-03")));
        var test6Treatment = treatmentDao.create(new TreatmentCreationData(test2ArchivedPatientWithTreatment.getId(), convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Test 6 Treatment, should be deleted", test6ArchivedCaregiverWithTreatment.getId(), 0, convertStringToLocalDate("2013-06-03")));
    }
}