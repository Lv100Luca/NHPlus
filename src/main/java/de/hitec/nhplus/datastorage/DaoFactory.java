package de.hitec.nhplus.datastorage;

/**
 * The <code>DaoFactory</code> creates all DAOs. It is a singleton class.
 */
public class DaoFactory {

    private static DaoFactory instance;

    private DaoFactory() {
    }

    /**
     * Returns the singleton instance of the <code>DaoFactory</code>.
     *
     * @return The singleton instance of the <code>DaoFactory</code>.
     */
    public static DaoFactory getDaoFactory() {
        if (DaoFactory.instance == null) {
            DaoFactory.instance = new DaoFactory();
        }
        return DaoFactory.instance;
    }

    /**
     * Creates a new <code>PatientDao</code> instance.
     *
     * @return The <code>PatientDao</code> instance.
     */
    public TreatmentDao createTreatmentDao() {
        return new TreatmentDao(ConnectionBuilder.getConnection());
    }

    /**
     * Creates a new <code>PatientDao</code> instance.
     *
     * @return The <code>PatientDao</code> instance.
     */
    public PatientDao createPatientDAO() {
        return new PatientDao(ConnectionBuilder.getConnection());
    }

    /**
     * Creates a new <code>PatientDao</code> instance.
     *
     * @return The <code>PatientDao</code> instance.
     */
    public CaregiverDao createCaregiverDAO() {
        return new CaregiverDao(ConnectionBuilder.getConnection());
    }

    /**
     * Creates a new <code>PatientDao</code> instance.
     *
     * @return The <code>PatientDao</code> instance.
     */
    public MedicineDao createMedicineDAO() {
        return new MedicineDao(ConnectionBuilder.getConnection());
    }

    /**
     * Creates a new <code>PatientDao</code> instance.
     *
     * @return The <code>PatientDao</code> instance.
     */
    public UserDao createUserDAO() {
        return new UserDao(ConnectionBuilder.getConnection());
    }
}
