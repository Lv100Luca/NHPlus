package de.hitec.nhplus.model.Exceptions;

/**
 * Exception class for exceptions thrown by the DAO when creating a new entity.
 */
public class CreateException extends DaoExeption {
    /**
     * Constructor for the <code>CreateException</code> class.
     *
     * @param message The message of the exception.
     */
    public CreateException(String message) {
        super(message);
    }
}
