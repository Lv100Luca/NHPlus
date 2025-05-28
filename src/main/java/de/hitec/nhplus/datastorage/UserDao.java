package de.hitec.nhplus.datastorage;

import de.hitec.nhplus.model.CreationData.UserCreationData;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.utils.HashPassword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDao extends DaoImp<User, UserCreationData>{

    public UserDao(final Connection connection) {
        super(connection);
    }

    @Override
    protected PreparedStatement getCreateStatement(final UserCreationData user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "INSERT INTO user (username, password) VALUES (?, ?)";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getReadByIDStatement(final long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM user WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected User getInstanceFromResultSet(final ResultSet result) throws SQLException {
        return User.fromResultSet(result);
    }

    @Override
    protected PreparedStatement getReadAllStatement() {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "SELECT * FROM user";
            preparedStatement = this.connection.prepareStatement(SQL);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }


    @Override
    protected ArrayList<User> getListFromResultSet(final ResultSet set) throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        while (set.next()) {
            list.add(getInstanceFromResultSet(set));
        }
        return list;
    }

    @Override
    protected PreparedStatement getUpdateStatement(final User user) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "UPDATE user SET " +
                    "username = ?, " +
                    "password = ?, " +
                    "WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, user.getId());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    @Override
    protected PreparedStatement getDeleteStatement(final long key) {
        PreparedStatement preparedStatement = null;
        try {
            final String SQL = "DELETE FROM user WHERE id = ?";
            preparedStatement = this.connection.prepareStatement(SQL);
            preparedStatement.setLong(1, key);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return preparedStatement;
    }

    public boolean doesUserExist(final String username) {
        String SQL = "SELECT 1 FROM user WHERE username = ? LIMIT 1";

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a result is found
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean isPasswordCorrect(final String username, final String password) {
        String SQL = "SELECT password FROM user WHERE username = ?";
        String hashedPassword = HashPassword.hashPassword(password);

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    return hashedPassword.equals(storedPassword);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

}
