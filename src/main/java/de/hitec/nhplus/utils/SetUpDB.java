package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.CreationData.CaregiverCreationData;
import de.hitec.nhplus.model.CreationData.MedicineCreationData;
import de.hitec.nhplus.model.CreationData.PatientCreationData;
import de.hitec.nhplus.model.CreationData.TreatmentCreationData;
import de.hitec.nhplus.datastorage.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalDate;
import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalTime;

/**
 * Call static class provides to static methods to set up and wipe the database. It uses the class ConnectionBuilder
 * and its path to build up the connection to the database. The class is executable. Executing the class will build
 * up a connection to the database and calls setUpDb() to wipe the database, build up a clean database and fill the
 * database with some test data.
 */
public class SetUpDB {

    /**
     * This method wipes the database by dropping the tables. Then the method calls DDL statements to build it up from
     * scratch and DML statements to fill the database with hard coded test data.
     */
    public static void setUpDb() {
        Connection connection = ConnectionBuilder.getConnection();
        SetUpDB.wipeDb(connection);

        SetUpDB.setUpTablePatient(connection);
        SetUpDB.setUpTableTreatment(connection);
        SetUpDB.setUpTableCaregiver(connection);
        SetUpDB.setUpTableMedicine(connection);

        SetUpDB.setUpPatients();
        SetUpDB.setUpTreatments();
        SetUpDB.setUpCaregivers();
        SetUpDB.setUpMedicines();
    }

    /**
     * Wipes the database by dropping the tables.
     *
     * @param connection current connection to the database
     */
    public static void wipeDb(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS caregiver");
            statement.execute("DROP TABLE IF EXISTS patient");
            statement.execute("DROP TABLE IF EXISTS treatment");
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Creates the patient table in the database.
     *
     * @param connection The connection to the database.
     */
    public static void setUpTablePatient(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS patient (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   dateOfBirth TEXT NOT NULL, " +
                "   carelevel TEXT NOT NULL, " +
                "   roomnumber TEXT NOT NULL, " +
                "   archivedOn TEXT" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Creates the treatment table in the database.
     *
     * @param connection The connection to the database.
     */
    public static void setUpTableTreatment(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS treatment (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   patientId INTEGER NOT NULL, " +
                "   treatment_date TEXT NOT NULL, " +
                "   begin TEXT NOT NULL, " +
                "   end TEXT NOT NULL, " +
                "   description TEXT NOT NULL, " +
                "   remark TEXT NOT NULL," +
                "   caregiverId INTEGER NOT NULL," +
                "   medicineId INTEGER," +
                "   archivedOn TEXT," +
                "   FOREIGN KEY (patientId) REFERENCES patient (id) ON DELETE CASCADE " +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Creates the caregiver table in the database.
     *
     * @param connection The connection to the database.
     */
    public static void setUpTableCaregiver(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS caregiver (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   phoneNumber TEXT NOT NULL, " +
                "   archivedOn TEXT" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Creates a new table <code>medicine</code> in the database.
     *
     * @param connection Object of class <code>Connection</code>.
     */
    private static void setUpTableMedicine(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS medicine (" +
                "   medicineId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   name TEXT NOT NULL, " +
                "   storage TEXT NOT NULL," +
                "   expirationDate TEXT NOT NULL" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Inserts some test data into the patient table.
     */
    private static void setUpPatients() {
        PatientDao dao = DaoFactory.getDaoFactory().createPatientDAO();
        if (dao.getAll().isEmpty()) {
            dao.create(new PatientCreationData("Seppl", "Herberger", convertStringToLocalDate("1945-12-01"), "4", "202", null));
            dao.create(new PatientCreationData("Martina", "Gerdsen", convertStringToLocalDate("1954-08-12"), "5", "010", null));
            dao.create(new PatientCreationData("Gertrud", "Franzen", convertStringToLocalDate("1949-04-16"), "3", "002", null));
            dao.create(new PatientCreationData("Ahmet", "Yilmaz", convertStringToLocalDate("1941-02-22"), "3", "013", convertStringToLocalDate("2000-06-03")));
            dao.create(new PatientCreationData("Hans", "Neumann", convertStringToLocalDate("1955-12-12"), "2", "001", null));
            dao.create(new PatientCreationData("Elisabeth", "Müller", convertStringToLocalDate("1958-03-07"), "5", "110", null));
        }
    }

    /**
     * Inserts some test data into the treatment table.
     */
    private static void setUpTreatments() {
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        if (dao.getAll().isEmpty()) {
            dao.create(new TreatmentCreationData(1, convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Der Patient hat enorme Angstgefühle und glaubt, er sei überfallen worden. Ihm seien alle Wertsachen gestohlen worden.\nPatient beruhigt sich erst, als alle Wertsachen im Zimmer gefunden worden sind.", 0, 1, null));
            dao.create(new TreatmentCreationData(1, convertStringToLocalDate("2023-06-05"), convertStringToLocalTime("11:00"), convertStringToLocalTime("12:30"), "Gespräch", "Patient irrt auf der Suche nach gestohlenen Wertsachen durch die Etage und bezichtigt andere Bewohner des Diebstahls.\nPatient wird in seinen Raum zurückbegleitet und erhält Beruhigungsmittel.", 4, 0, null));
            dao.create(new TreatmentCreationData(2, convertStringToLocalDate("2023-06-04"), convertStringToLocalTime("07:30"), convertStringToLocalTime("08:00"), "Waschen", "Patient mit Waschlappen gewaschen und frisch angezogen. Patient gewendet.", 5, 4, null));
            dao.create(new TreatmentCreationData(1, convertStringToLocalDate("2023-06-06"), convertStringToLocalTime("15:10"), convertStringToLocalTime("16:00"), "Spaziergang", "Spaziergang im Park, Patient döst  im Rollstuhl ein", 3, 6, null));
            dao.create(new TreatmentCreationData(1, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("16:00"), "Spaziergang", "Parkspaziergang; Patient ist heute lebhafter und hat klare Momente; erzählt von seiner Tochter", 0, 4, null));
            dao.create(new TreatmentCreationData(2, convertStringToLocalDate("2023-06-07"), convertStringToLocalTime("11:00"), convertStringToLocalTime("11:30"), "Waschen", "Waschen per Dusche auf einem Stuhl; Patientin gewendet;", 2, 3, null));
            dao.create(new TreatmentCreationData(5, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("15:30"), "Physiotherapie", "Übungen zur Stabilisation und Mobilisierung der Rückenmuskulatur", 0, 7, null));
            dao.create(new TreatmentCreationData(4, convertStringToLocalDate("2023-08-24"), convertStringToLocalTime("09:30"), convertStringToLocalTime("10:15"), "KG", "Lympfdrainage", 3, 7, null));
            dao.create(new TreatmentCreationData(6, convertStringToLocalDate("2023-08-31"), convertStringToLocalTime("13:30"), convertStringToLocalTime("13:45"), "Toilettengang", "Hilfe beim Toilettengang; Patientin klagt über Schmerzen beim Stuhlgang. Gabe von Iberogast", 5, 3, null));
            dao.create(new TreatmentCreationData(6, convertStringToLocalDate("2023-09-01"), convertStringToLocalTime("16:00"), convertStringToLocalTime("17:00"), "KG", "Massage der Extremitäten zur Verbesserung der Durchblutung", 5, 1, null));
        }
    }

    /**
     * Inserts some test data into the caregiver table.
     */
    private static void setUpCaregivers() {
        CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();
        if (dao.getAll().isEmpty()) {
            dao.create(new CaregiverCreationData("Hans", "Müller", "+49 176 12345678", null));
            dao.create(new CaregiverCreationData("Peter", "Schmidt", "+49 176 23456789", null));
            dao.create(new CaregiverCreationData("Maria", "Meier", "+49 176 34567890", null));
            dao.create(new CaregiverCreationData("Anna", "Schneider", "+49 176 45678901", null));
        }
    }

    /**
     * Fills the database with some medicines.
     */
    private static void setUpMedicines() {
        MedicineDao dao = DaoFactory.getDaoFactory().createMedicineDAO();
        dao.create(new MedicineCreationData("Amoxicillin 500mg", "Shelf A", convertStringToLocalDate("2026-03-15")));
        dao.create(new MedicineCreationData("Lisinopril 10mg", "Shelf B", convertStringToLocalDate("2025-11-30")));
        dao.create(new MedicineCreationData("Ibuprofen 200mg", "Shelf C", convertStringToLocalDate("2027-01-20")));
        dao.create(new MedicineCreationData("Metformin 500mg", "Shelf A", convertStringToLocalDate("2025-08-10")));
        dao.create(new MedicineCreationData("Simvastatin 20mg", "Shelf B", convertStringToLocalDate("2026-06-25")));
        dao.create(new MedicineCreationData("Omeprazole 20mg", "Shelf C", convertStringToLocalDate("2025-12-05")));
        dao.create(new MedicineCreationData("Albuterol Inhaler", "Shelf A", convertStringToLocalDate("2026-04-01")));
        dao.create(new MedicineCreationData("Sertraline 50mg", "Shelf B", convertStringToLocalDate("2027-02-18")));
        dao.create(new MedicineCreationData("Loratadine 10mg", "Shelf C", convertStringToLocalDate("2026-10-11")));
        dao.create(new MedicineCreationData("Prednisone 5mg", "Shelf A", convertStringToLocalDate("2025-09-22")));
    }

    /**
     * Main method to run the SetUpDB class.
     * <br>
     * Sets up the database tables and fills them with some test data.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SetUpDB.setUpDb();
    }
}
