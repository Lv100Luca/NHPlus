package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User implements Entity {

    private final SimpleLongProperty id;
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;

    public User(long id, String username, String password) {
        this.id = new SimpleLongProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

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
