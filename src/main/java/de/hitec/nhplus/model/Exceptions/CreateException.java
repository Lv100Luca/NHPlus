package de.hitec.nhplus.model.Exceptions;

/**
 * Exception class for exceptions thrown by the DAO when creating a new entity.
 */
public class CreateException extends DaoExeption {
    public CreateException(String message) {
        super(message);
    }
}
