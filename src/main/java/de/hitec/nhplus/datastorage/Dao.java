package de.hitec.nhplus.datastorage;

import java.util.List;
import java.util.Optional;

/**
 * Interface for all DAOs. It defines methods to create, read, update and delete objects from the database.
 *
 * @param <TEntity> Type of the object to be stored in the database.
 */
public interface Dao<TEntity, TCreationData> {
    /**
     * Creates a new object of type <code>T</code> in the database.
     *
     * @param entity Object to be stored in the database.
     * @return The object with the given id if it exists, otherwise an empty optional.
     */
    TEntity create(TCreationData entity);

    /**
     * Gets an object of type <code>T</code> from the database by its id.
     *
     * @param id ID of the object to be retrieved.
     * @return The object with the given id if it exists, otherwise an empty optional.
     */
    Optional<TEntity> getById(long id);

    /**
     * Gets all objects of type <code>T</code> from the database.
     *
     * @return List of all objects of type <code>T</code>.
     */
    List<TEntity> getAll();

    /**
     * Updates an object of type <code>T</code> in the database.
     *
     * @param entity Object to be updated in the database.
     * @return The updated object.
     */
    TEntity update(TEntity entity);

    /**
     * Deletes an object of type <code>T</code> from the database.
     *
     * @param id The id of the object to be deleted.
     * @return The deleted object.
     */
    Optional<TEntity> delete(long id);
}
