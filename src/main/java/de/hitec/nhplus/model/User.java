package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a user in the database.
 */
public class User implements Entity {

    private final SimpleLongProperty id;
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;

    /**
     * Private constructor to create a new user from the <code>fromResultSet</code> method.
     *
     * @param id      The id of the user.
     * @param username The username of the user.
     * @param password The password of the user.
     */
    private User(long id, String username, String password) {
        this.id = new SimpleLongProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    /**
     * Creates a new user from a result set.
     * Ensures that entities can only be created from result sets that came from the database.
     *
     * @param result The result set to create the user from.
     * @return The user created from the result set.
     * @throws SQLException If the result set is empty.
     */
    public static User fromResultSet(ResultSet result) throws SQLException {
        return new User(result.getLong(1), result.getString(2),
                result.getString(3));
    }

    public long getId() {
        return id.get();
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(final String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(final String password) {
        this.password.set(password);
    }
}
