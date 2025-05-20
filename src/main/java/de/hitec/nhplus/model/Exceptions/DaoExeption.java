package de.hitec.nhplus.model.Exceptions;

/**
 * Exception class for exceptions thrown by the DAO.
 */
public class DaoExeption extends RuntimeException {
    public DaoExeption(String message) {
        super(message);
    }
}
