package de.hitec.nhplus.model.Exceptions;

/**
 * Exception class for exceptions thrown by the DAO.
 */
public class DaoExeption extends RuntimeException {
    /**
     * Constructor for the <code>DaoExeption</code> class.
     *
     * @param message The message of the exception.
     */
    public DaoExeption(String message) {
        super(message);
    }
}
