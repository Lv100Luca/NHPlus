package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * The <code>LoginController</code> contains the entire logic of the login view. It determines which data is displayed and how to react to events.
 */
public class LoginController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField textFieldUserName;

    @FXML
    private TextField textFieldPassword;

    private int wrongPasswordCount = 0;
    private Timeline lockoutTimer;
    private long lockoutStartTime = 0;
    // duration = minutes * seconds * milliseconds
    private final double lockoutDuration = 2 * 60 * 1000;

    private UserDao userDao;

    /**
     * Initializes the controller class. It gets all medicines from the database and displays them in the combo box.
     */
    public void initialize() {
        userDao = DaoFactory.getDaoFactory().createUserDAO();

        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    /**
     * Handles the event of logging in. It checks if the username and password are correct and logs in the user if they are.
     */
    private void accessApplication(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            Parent mainRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(mainRoot);
            stage.setScene(scene);
            stage.setTitle("NHPlus");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the event of logging in. It checks if the username and password are correct and logs in the user if they are.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        errorLabel.setVisible(false);
        if (wrongPasswordCount >= 3) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lockoutStartTime < lockoutDuration) {
                errorLabel.setText("Too many wrong passwords!" +
                        "\nPlease wait");
                errorLabel.setVisible(true);

                return;
            } else {
                resetLockout();
            }
        }
        if (isLoginSuccessful()) {
            accessApplication(event);
        }
    }

    /**
     * Checks if the username and password are correct.
     *
     * @return <code>true</code> if the username and password are correct, <code>false</code> otherwise.
     */
    private boolean isLoginSuccessful() {
        boolean validUser = userExists(textFieldUserName.getText());
        boolean validPassword = correctPassword(textFieldUserName.getText(), textFieldPassword.getText());

        if (!validUser) {
            errorLabel.setText("Invalid username");
            errorLabel.setVisible(true);
            return false;
        }
        if (!validPassword) {
            wrongPasswordCount++;
            errorLabel.setText("Wrong password");
            errorLabel.setVisible(true);

            if (wrongPasswordCount >= 3) {
                errorLabel.setVisible(false);
                lockoutStartTime = System.currentTimeMillis();
                startLockoutTimer();
                errorLabel.setText("Too many wrong passwords!" +
                        "\nPlease wait");
                errorLabel.setVisible(true);
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if the username exists in the database.
     *
     * @param username The username to check.
     * @return <code>true</code> if the username exists, <code>false</code> otherwise.
     */
    private boolean userExists(String username) {
        return username != null
                && !username.isEmpty()
                && userDao.doesUserExist(username);
    }

    /**
     * Checks if the password is correct.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return <code>true</code> if the password is correct, <code>false</code> otherwise.
     */
    private boolean correctPassword(String username, String password) {
        return password != null
                && !password.isEmpty()
                && userDao.isPasswordCorrect(username, password);
    }

    /**
     * Starts the lockout timer.
     */
    private void startLockoutTimer() {
        if (lockoutTimer != null) {
            lockoutTimer.stop();
        }
        lockoutTimer = new Timeline(new KeyFrame(Duration.millis(lockoutDuration), e -> resetLockout()));
        lockoutTimer.setCycleCount(1);
        lockoutTimer.play();
    }

    /**
     * Resets the lockout timer.
     */
    private void resetLockout() {
        wrongPasswordCount = 0;
        lockoutStartTime = 0;
        errorLabel.setVisible(false);
    }
}
