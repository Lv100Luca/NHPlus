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

    protected abstract T getInstanceFromResultSet(ResultSet set) throws SQLException;

    protected abstract ArrayList<T> getListFromResultSet(ResultSet set) throws SQLException;

    protected abstract PreparedStatement getCreateStatement(TCreationData t);

    protected abstract PreparedStatement getReadByIDStatement(long key);

    protected abstract PreparedStatement getReadAllStatement();

    protected abstract PreparedStatement getUpdateStatement(T t);

    protected abstract PreparedStatement getDeleteStatement(long key);

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
        try {
            final String SQL = "UPDATE " + tableName + " SET archivedOn = ? WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, DateConverter.convertLocalDateToString(archivedOn));
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
