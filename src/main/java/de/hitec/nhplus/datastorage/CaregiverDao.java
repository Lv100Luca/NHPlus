package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.CreationData.CaregiverCreationData;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static de.hitec.nhplus.datastorage.PatientDao.TABLE_NAME;

/**
 * The <code>CaregiverDao</code> extends the {@link DaoImp} class and provides methods to access the caregiver table.
 */
public class CaregiverDao extends DaoImp<Caregiver, CaregiverCreationData>{
    public static final String TABLE_NAME = "caregiver";

    /**
     * The constructor initiates an object of <code>CaregiverDao</code> and passes the connection to its super class.
     *
     * @param connection Object of <code>Connection</code> to execute the SQL-statements.
     */
    public CaregiverDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(CaregiverCreationData caregiver) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO caregiver (firstname, surname, phoneNumber, archivedOn) " +
                    "VALUES (?, ?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.firstName());
            preparedStatement.setString(2, caregiver.surname());
            preparedStatement.setString(3, caregiver.phoneNumber());
            preparedStatement.setString(4, caregiver.archivedOn() == null ? null : caregiver.archivedOn().toString());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM caregiver WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected Caregiver getInstanceFromResultSet(ResultSet result) throws SQLException {
        return Caregiver.fromResultSet(result);
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM caregiver";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Gets all archived caregivers from the database.
     *
     * @return All caregivers that are archived.
     */
    public ArrayList<Caregiver> getAllArchived() {
        try {
            final String SQL = "SELECT * FROM caregiver WHERE archivedOn IS NOT NULL";
            ResultSet result = this.connection.prepareStatement(SQL).executeQuery();

            return getListFromResultSet(result);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Gets all not archived caregivers from the database.
     *
     * @return All caregivers that are not archived.
     */
    public ArrayList<Caregiver> getAllNotArchived() {
        try {
            final String SQL = "SELECT * FROM caregiver WHERE archivedOn IS NULL";
            ResultSet result = this.connection.prepareStatement(SQL).executeQuery();

            return getListFromResultSet(result);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Override
    protected ArrayList<Caregiver> getListFromResultSet(ResultSet set) throws SQLException {
        ArrayList<Caregiver> list = new ArrayList<>();
        while (set.next()) {
            list.add(getInstanceFromResultSet(set));
        }
        return list;
    }

    @Override
    protected PreparedStatement getUpdateStatement(Caregiver caregiver) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE caregiver SET " +
                                    "firstname = ?, " +
                                    "surname = ?, " +
                                    "phoneNumber = ? " +
                                    "WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, caregiver.getFirstName());
            preparedStatement.setString(2, caregiver.getSurname());
            preparedStatement.setString(3, caregiver.getPhoneNumber());
            preparedStatement.setLong(4, caregiver.getId());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM caregiver WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    /**
     * Archives a caregiver with the given id.
     *
     * @param cid Caregiver id to archive.
     */
    public void archive(long cid) {
        super.archive(TABLE_NAME, cid);
    }

    /**
     * Restores a caregiver with the given id.
     *
     * @param cid Caregiver id to restore.
     */
    public void restore(long cid) {
        super.restore(TABLE_NAME, cid);
    }
}
