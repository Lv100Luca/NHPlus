package de.hitec.nhplus.model.Exceptions;

/**
 * Exception class for exceptions thrown by the DAO when updating an entity.
 */
public class UpdateException extends DaoExeption {
    public UpdateException(String message) {
        super(message);
    }
}
