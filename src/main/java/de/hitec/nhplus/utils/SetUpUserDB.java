package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.CreationData.UserCreationData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Call static class provides to static methods to set up and wipe the database. It uses the class ConnectionBuilder
 * and its path to build up the connection to the database. The class is executable. Executing the class will build
 * up a connection to the database and calls setUpUserDb() to wipe the database, build up a clean database and fill the
 * database with some test data.
 */
public class SetUpUserDB {

    /**
     * This method wipes the database by dropping the tables. Then the method calls DDL statements to build it up from
     * scratch and DML statements to fill the database with hard coded test data.
     */
    public static void setUpUserDB() {
        Connection connection = ConnectionBuilder.getConnection();
        SetUpUserDB.wipeDb(connection);

        SetUpUserDB.setUpTableUsers(connection);

        SetUpUserDB.setUpUsers();
    }

    /**
     * This method wipes the database by dropping the tables.
     */
    public static void wipeDb(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS user");
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Creates the user table in the database.
     *
     * @param connection The connection to the database.
     */
    private static void setUpTableUsers(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS user (" +
                "   id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   username TEXT NOT NULL, " +
                "   password TEXT NOT NULL " +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Inserts some test data into the user table.
     */
    private static void setUpUsers() {
        UserDao dao = DaoFactory.getDaoFactory().createUserDAO();
        if (dao.getAll().isEmpty()) {
            dao.create(new UserCreationData("Luca", HashPassword.hashPassword("15 Hogrider!")));
            dao.create(new UserCreationData("Leon", HashPassword.hashPassword("420")));
            dao.create(new UserCreationData("Matthes", HashPassword.hashPassword("Mega Knight")));
            dao.create(new UserCreationData("b.heidemann", HashPassword.hashPassword("NH_PLUS")));
        }
    }

    /**
     * Main method to run the SetUpUserDB class.
     * <br>
     * Sets up the database tables and fills them with some test data.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SetUpUserDB.setUpUserDB();
    }
}
