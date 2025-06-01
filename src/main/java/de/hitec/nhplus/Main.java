package de.hitec.nhplus;

import de.hitec.nhplus.Services.ArchiveService;
import de.hitec.nhplus.datastorage.ConnectionBuilder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainWindow();
    }

    public void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            AnchorPane pane = loader.load();

            Scene scene = new Scene(pane);
            this.primaryStage.setTitle("NHPlus");
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();

            this.primaryStage.setOnCloseRequest(event -> {
                ConnectionBuilder.closeConnection();
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var archiveService = ArchiveService.getInstance();

        // delete old archived entries
        var deletedTreatments = archiveService.deleteOldTreatments();
        System.out.println("Deleted " + deletedTreatments + " archived treatments.");

        var deletedPatients = archiveService.deleteOldPatients();
        System.out.println("Deleted " + deletedPatients + " archived patients.");

        var deletedCaregivers = archiveService.deleteOldCaregivers();
        System.out.println("Deleted " + deletedCaregivers + " archived caregivers.");

        launch(args);
    }
}