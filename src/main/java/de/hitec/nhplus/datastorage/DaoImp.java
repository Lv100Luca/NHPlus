package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
                throw new RuntimeException("Could not create new entity");

            return entity.get();
        } catch (SQLException exception) {
            // creating a new object _should_ never fail
            // highlights underlying issue
            throw new RuntimeException(exception);
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
            // updating an existing object _should_ never fail
            // highlights underlying issue
            throw new RuntimeException(exception);
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
}
