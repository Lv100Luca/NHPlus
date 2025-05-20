package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.Medicine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class AllTreatmentController {

    @FXML
    private TableView<Treatment> tableView;

    @FXML
    private TableColumn<Treatment, Integer> columnId;

    @FXML
    private TableColumn<Treatment, Long> columnPatient;

    @FXML
    private TableColumn<Treatment, String> columnDate;

    @FXML
    private TableColumn<Treatment, String> columnBegin;

    @FXML
    private TableColumn<Treatment, String> columnEnd;

    @FXML
    private TableColumn<Treatment, String> columnDescription;

    @FXML
    private TableColumn<Treatment, Long> columnCaregiver;

    @FXML
    private TableColumn<Medicine, Long> columnMedicine;

    @FXML
    private ComboBox<String> comboBoxPatientSelection;

    private final ObservableList<String> patientSelection = FXCollections.observableArrayList();

    @FXML
    private Button buttonDelete;

    private final ObservableList<Treatment> treatments = FXCollections.observableArrayList();

    private ArrayList<Patient> patientList;

    private TreatmentDao treatmentDao;
    private CaregiverDao caregiverDao;
    private PatientDao patientDao;
    private MedicineDao medicineDao;


    public void initialize() {
        treatmentDao = DaoFactory.getDaoFactory().createTreatmentDao();
        caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
        patientDao = DaoFactory.getDaoFactory().createPatientDAO();
        medicineDao = DaoFactory.getDaoFactory().createMedicineDAO();

        readAllAndShowInTableView();
        comboBoxPatientSelection.setItems(patientSelection);
        comboBoxPatientSelection.getSelectionModel().select(0);

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.columnPatient.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.columnPatient.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(getDisplayText(item, empty));
            }
        });

        this.columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.columnBegin.setCellValueFactory(new PropertyValueFactory<>("begin"));
        this.columnEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        this.columnCaregiver.setCellValueFactory(new PropertyValueFactory<>("cid"));
        this.columnCaregiver.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(getDisplayText(item, empty));
            }
        });

        this.columnMedicine.setCellValueFactory(new PropertyValueFactory<>("mid"));
        this.columnMedicine.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(getMedicineDisplayText(item, empty));
            }
        });

        this.tableView.setItems(this.treatments);

        // Disabling the button to delete treatments as long, as no treatment was selected.
        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldTreatment, newTreatment) ->
                        AllTreatmentController.this.buttonDelete.setDisable(newTreatment == null));

        this.createComboBoxData();
    }

    private String getDisplayText(Long item, boolean empty) {
        if (empty) return "";

        return Optional.ofNullable(item)
                .flatMap(patientDao::getById)
                .map(Patient::getFullName)
                .orElse(" - ");
    }

    private String getMedicineDisplayText(Long item, boolean empty) {
        if (empty) return "";

        if (item == null)
            return " - ";

        var medicine = medicineDao.getById(item);
        if (medicine.isEmpty())
            return " - ";

        return medicine.get().getName();
    }

    public void readAllAndShowInTableView() {
        this.treatments.clear();
        comboBoxPatientSelection.getSelectionModel().select(0);

        this.treatments.addAll(treatmentDao.getAll());
    }

    private void createComboBoxData() {
        patientList = patientDao.getAll();
        this.patientSelection.add("alle");

        for (Patient patient : patientList) {
            this.patientSelection.add(patient.getSurname());
        }
    }


    @FXML
    public void handleComboBox() {
        String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
        this.treatments.clear();

        if (selectedPatient.equals("alle")) {
            this.treatments.addAll(this.treatmentDao.getAll());
        }

        Patient patient = searchInList(selectedPatient);
        if (patient != null) {
            try {
                this.treatments.addAll(this.treatmentDao.readTreatmentsByPid(patient.getId()));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private Patient searchInList(String surname) {
        for (Patient patient : this.patientList) {
            if (patient.getSurname().equals(surname)) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Deletes the selected treatment from the database and removes it from the list of treatments.
     * If the treatment was deleted successfully, the list of treatments is updated.
     */
    @FXML
    public void handleDelete() { //todo test me
        var treatment = this.tableView.getSelectionModel().getSelectedItem();
        var deleted = treatmentDao.delete(treatment.getId());

        deleted.ifPresent(this.treatments::remove);
    }

    @FXML
    public void handleNewTreatment() {
        try {
            String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
            Patient patient = searchInList(selectedPatient);
            newTreatmentWindow(patient);
        } catch (NullPointerException exception) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Patient für die Behandlung fehlt!");
            alert.setContentText("Wählen Sie über die Combobox einen Patienten aus!");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleMouseClick() {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (tableView.getSelectionModel().getSelectedItem() != null)) {
                int index = this.tableView.getSelectionModel().getSelectedIndex();
                Treatment treatment = this.treatments.get(index);
                treatmentWindow(treatment);
            }
        });
    }

    public void newTreatmentWindow(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/NewTreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();

            NewTreatmentController controller = loader.getController();
            controller.initialize(this, stage, patient);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void treatmentWindow(Treatment treatment) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/TreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();
            TreatmentController controller = loader.getController();
            controller.initializeController(this, stage, treatment);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
