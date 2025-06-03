package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Entity;
import de.hitec.nhplus.model.Exceptions.CreateException;
import de.hitec.nhplus.model.Exceptions.UpdateException;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Abstract class for all DAOs. It defines methods to create, read, update and delete objects from the database.
 *
 * @param <T> Type of the object to be stored in the database.
 * @param <TCreationData> Type of the object to be used for creation.
 */
// add TCreationData from Dao interface
public abstract class DaoImp<T extends Entity, TCreationData> implements Dao<T, TCreationData> {
    protected Connection connection;

    /**
     * Initializes the DAO class. It gets the connection from the <code>ConnectionBuilder</code>.
     *
     * @param connection Current connection to the database.
     */
    public DaoImp(Connection connection) {
        this.connection = connection;
    }

    @Override
    public T create(TCreationData data) {
        try {
            getCreateStatement(data).executeUpdate();

            var insertedId = getLastInsertedId();

            Optional<T> entity = getById(insertedId);
            if (entity.isEmpty())
                throw new CreateException("Could not create new entity");

            return entity.get();
        } catch (SQLException exception) {
            // creating a new object _should_ never fail
            // highlights underlying issue
            throw new CreateException(exception.getMessage());
        }
    }

    @Override
    public Optional<T> getById(long id) {
        try {
            T object = null;
            ResultSet result = getReadByIDStatement(id).executeQuery();
            if (result.next()) {
                object = getInstanceFromResultSet(result);
            }
            return Optional.ofNullable(object);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<T> getAll() {
        try {
            return getListFromResultSet(getReadAllStatement().executeQuery());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public T update(T entity) {
        try {
            getUpdateStatement(entity).executeUpdate();
            return entity;
        } catch (SQLException exception) {
            throw new UpdateException(exception.getMessage());
        }
    }

    @Override
    public Optional<T> delete(long id) {
        Optional<T> entity = getById(id);

        if (entity.isPresent()) {
            try {
                getDeleteStatement(id).executeUpdate();
                return entity;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the instance of <code>T</code> from the result set.
     *
     * @param set Result set to get the instance from.
     * @return Instance of <code>T</code> from the result set.
     * @throws SQLException If the result set is empty.
     */
    protected abstract T getInstanceFromResultSet(ResultSet set) throws SQLException;

    /**
     * Returns a list of <code>T</code> from the result set.
     *
     * @param set Result set to get the list from.
     * @return List of <code>T</code> from the result set.
     * @throws SQLException If the result set is empty.
     */
    protected abstract ArrayList<T> getListFromResultSet(ResultSet set) throws SQLException;

    /**
     * Returns the prepared statement for creating a new object.
     *
     * @param t The object to create.
     * @return The prepared statement for creating a new object.
     */
    protected abstract PreparedStatement getCreateStatement(TCreationData t);

    /**
     * Returns the prepared statement for reading an object by its id.
     *
     * @param key The id of the object to read.
     * @return The prepared statement for reading an object by its id.
     */
    protected abstract PreparedStatement getReadByIDStatement(long key);

    /**
     * Returns the prepared statement for reading all objects.
     *
     * @return The prepared statement for reading all objects.
     */
    protected abstract PreparedStatement getReadAllStatement();

    /**
     * Returns the prepared statement for updating an object.
     *
     * @param t The object to update.
     * @return The prepared statement for updating an object.
     */
    protected abstract PreparedStatement getUpdateStatement(T t);

    /**
     * Returns the prepared statement for deleting an object.
     *
     * @param key The id of the object to delete.
     * @return The prepared statement for deleting an object.
     */
    protected abstract PreparedStatement getDeleteStatement(long key);

    /**
     * Helper method to get the last inserted id.
     *
     * @return The id of the last inserted entity into the database.
     */
    private int getLastInsertedId() {
        try {
            return this.connection.prepareStatement("SELECT last_insert_rowid();").executeQuery().getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * Sets the archivedOn field of the object with the given id to the given date.
     *
     * @param tableName Name of the table to set the archivedOn field.
     * @param id        Id of the object to set the archivedOn field.
     * @param archivedOn Date to set the archivedOn field to.
     */
    private void setArchivedOn(String tableName, long id, LocalDate archivedOn) {
        String archivedOnString = archivedOn == null ? null : DateConverter.convertLocalDateToString(archivedOn);
        try {
            final String SQL = "UPDATE " + tableName + " SET archivedOn = ? WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, archivedOnString);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * archives a object with the given id.
     *
     * @param tableName Name of the table to archive.
     * @param id        Id of the object to archive.
     */
    public void archive(String tableName, long id) {
        setArchivedOn(tableName, id, LocalDate.now());
    }

    /**
     * restores a object with the given id.
     *
     * @param tableName Name of the table to restore.
     * @param id        Id of the object to restore.
     */
    public void restore(String tableName, long id) {
        setArchivedOn(tableName, id, null);
    }
}
