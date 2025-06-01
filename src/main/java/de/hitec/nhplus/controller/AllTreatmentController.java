package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Entity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

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

    @FXML
    private CheckBox checkBoxShowArchived;

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
        patientSelection.add("alle");
        comboBoxPatientSelection.setItems(patientSelection);
        comboBoxPatientSelection.getSelectionModel().select(0);

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.columnPatient.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.columnPatient.setCellFactory(col -> new TableCell<>() {
            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(getDisplayText(item, empty, patientDao, Patient::getFullName));
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
                setText(getDisplayText(item, empty, caregiverDao, Caregiver::getFullName));
                setText(getDisplayText(item, empty, caregiverDao, Caregiver::getFullName));
            }
        });

        this.columnMedicine.setCellValueFactory(new PropertyValueFactory<>("mid"));
        this.columnMedicine.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(getDisplayText(item, empty, medicineDao, Medicine::getName));
            }
        });

        this.tableView.setItems(this.treatments);

        this.tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Treatment treatment, boolean empty) {
                super.updateItem(treatment, empty);
                if (treatment == null || empty) {
                    setStyle("");
                } else if (treatment.isArchived()) {
                    // Style for archived treatments
                    setStyle("-fx-background-color: lightgray; -fx-font-style: italic;");
                }
            }
        });

        // Disabling the button to delete treatments as long, as no treatment was selected.
        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Treatment>() {
            @Override
            public void changed(ObservableValue<? extends Treatment> observableValue, Treatment treatment, Treatment newTreatment) {
                AllTreatmentController.this.buttonDelete.setDisable(newTreatment == null);
                if (newTreatment == null)
                    return;

                AllTreatmentController.this.buttonDelete.setText(newTreatment.isArchived() ? "Wiederherstellen" : "Löschen");
            }
        });

        this.createComboBoxData();

        this.checkBoxShowArchived.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            // filter treatments based on patient selection
            var filteredTreatments = getTreatments();
            var patient = searchInList(comboBoxPatientSelection.getSelectionModel().getSelectedItem());

            if (patient != null) {
                filteredTreatments.removeIf(treatment -> treatment.getPid() != patient.getId());
            }

            this.treatments.clear();
            this.treatments.addAll(filteredTreatments);
        });
    }

    // todo: move to utils and reuse
    private <T extends Entity> String getDisplayText(Long item, boolean empty, Dao<T, ?> dao, Function<T, String> valueFunction) {
        if (empty) return "";
        return Optional.ofNullable(item)
                .flatMap(dao::getById)
                .map(valueFunction)
                .orElse(" - ");
    }

    public void readAllAndShowInTableView() {
        this.treatments.clear();
        comboBoxPatientSelection.getSelectionModel().select(0);

        this.treatments.addAll(getTreatments());
    }

    private void createComboBoxData() {
        patientList = patientDao.getAllNotArchived();

        for (Patient patient : patientList) {
            this.patientSelection.add(patient.getSurname());
        }
    }


    @FXML
    public void handleComboBox() {
        String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
        this.treatments.clear();

        if (selectedPatient.equals("alle")) {
            this.treatments.addAll(getTreatments());
        }

        Patient patient = searchInList(selectedPatient);
        if (patient != null) {
            this.treatments.addAll(getTreatmentsByPatient(patient));
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

        if (treatment == null)
            return;

        if (treatment.isArchived())
            treatmentDao.restore(treatment.getId());
        else
            treatmentDao.archive(treatment.getId());

        readAllAndShowInTableView();
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

    private ArrayList<Treatment> getTreatments() {
        ArrayList<Treatment> treatments = new ArrayList<>();

        if (checkBoxShowArchived.isSelected())
            treatments.addAll(treatmentDao.getAll());
        else
            treatments.addAll(treatmentDao.getAllNotArchived());

        return treatments;
    }

    private ArrayList<Treatment> getTreatmentsByPatient(Patient patient) {
        ArrayList<Treatment> treatments = new ArrayList<>();

        try {
            treatments.addAll(treatmentDao.readTreatmentsByPid(patient.getId()));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (!checkBoxShowArchived.isSelected())
            treatments.removeIf(Treatment::isArchived);

        return treatments;
    }
}
