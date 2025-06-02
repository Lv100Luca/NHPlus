package de.hitec.nhplus.model.Exceptions;

/**
 * Exception class for exceptions thrown by the DAO when updating an entity.
 */
public class UpdateException extends DaoExeption {
    /**
     * Constructor for the <code>UpdateException</code> class.
     *
     * @param message The message of the exception.
     */
    public UpdateException(String message) {
        super(message);
    }
}
