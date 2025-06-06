package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.CreationData.MedicineCreationData;
import de.hitec.nhplus.model.Medicine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Implements the Interface <code>DaoImp</code>. Overrides methods to generate specific <code>PreparedStatements</code>,
 * to execute the specific SQL Statements.
 */
public class MedicineDao extends DaoImp<Medicine, MedicineCreationData> {

    /**
     * The constructor initiates an object of <code>MedicineDao</code> and passes the connection to its super class.
     *
     * @param connection Object of <code>Connection</code> to execute the SQL-statements.
     */
    public MedicineDao(Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(MedicineCreationData medicine) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO medicine (name, storage, expirationDate) " +
                    "VALUES (?, ?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, medicine.name());
            preparedStatement.setString(2, medicine.storage());
            preparedStatement.setString(3, medicine.expirationDate().toString());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM medicine WHERE medicineId = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected Medicine getInstanceFromResultSet(ResultSet set) throws SQLException {
        return Medicine.fromResultSet(set);
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM medicine";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected ArrayList<Medicine> getListFromResultSet(ResultSet set) throws SQLException {
        ArrayList<Medicine> list = new ArrayList<>();
        while (set.next()) {
            Medicine medicine = Medicine.fromResultSet(set);
            list.add(medicine);
        }
        return list;
    }


    @Override
    protected PreparedStatement getUpdateStatement(Medicine medicine) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE medicine SET " +
                    "name = ?, " +
                    "storage = ?, " +
                    "expirationDate = ? " +
                    "WHERE medicineId = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, medicine.getName());
            preparedStatement.setString(2, medicine.getStorage());
            preparedStatement.setString(3, medicine.getExpirationDate().toString());
            preparedStatement.setLong(4, medicine.getId());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM medicine WHERE medicineId = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }
}