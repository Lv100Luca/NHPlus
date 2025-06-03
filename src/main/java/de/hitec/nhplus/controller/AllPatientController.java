package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.model.CreationData.PatientCreationData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.utils.DateConverter;

import java.time.LocalDate;
import java.util.function.BiConsumer;


/**
 * The <code>AllPatientController</code> contains the entire logic of the patient view. It determines which data is displayed and how to react to events.
 */
public class AllPatientController {

    @FXML
    private TableView<Patient> tableView;

    @FXML
    private TableColumn<Patient, Integer> columnId;

    @FXML
    private TableColumn<Patient, String> columnFirstName;

    @FXML
    private TableColumn<Patient, String> columnSurname;

    @FXML
    private TableColumn<Patient, String> columnDateOfBirth;

    @FXML
    private TableColumn<Patient, String> columnCareLevel;

    @FXML
    private TableColumn<Patient, String> columnRoomNumber;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldDateOfBirth;

    @FXML
    private TextField textFieldCareLevel;

    @FXML
    private TextField textFieldRoomNumber;

    @FXML
    private CheckBox checkBoxShowArchived;

    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private PatientDao patientDao;

    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */
    public void initialize() {
        patientDao = DaoFactory.getDaoFactory().createPatientDAO();

        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // CellValueFactory to show property values in TableView
        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        // CellFactory to write property values from with in the TableView
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnDateOfBirth.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        this.columnDateOfBirth.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnCareLevel.setCellValueFactory(new PropertyValueFactory<>("careLevel"));
        this.columnCareLevel.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        this.columnRoomNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        //Anzeigen der Daten
        this.tableView.setItems(this.patients);

        this.tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (patient == null || empty) {
                    setStyle("");
                } else if (patient.isArchived()) {
                    // Style for archived patients
                    setStyle("-fx-background-color: lightgray; -fx-font-style: italic;");
                }
            }
        });

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Patient>() {
            @Override
            public void changed(ObservableValue<? extends Patient> observableValue, Patient oldPatient, Patient newPatient) {;
                AllPatientController.this.buttonDelete.setDisable(newPatient == null);

                if (newPatient == null)
                    return;

                AllPatientController.this.buttonDelete.setText(newPatient.isArchived() ? "Wiederherstellen" : "Löschen");
            }
        });

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewPatientListener = (observableValue, oldText, newText) ->
                AllPatientController.this.buttonAdd.setDisable(!AllPatientController.this.areInputDataValid());

        this.textFieldFirstName.textProperty().addListener(inputNewPatientListener);
        this.textFieldSurname.textProperty().addListener(inputNewPatientListener);
        this.textFieldDateOfBirth.textProperty().addListener(inputNewPatientListener);
        this.textFieldCareLevel.textProperty().addListener(inputNewPatientListener);
        this.textFieldRoomNumber.textProperty().addListener(inputNewPatientListener);

        columnFirstName.setOnEditCommit(event -> handleOnEdit(event, Patient::setFirstName));
        columnSurname.setOnEditCommit(event -> handleOnEdit(event, Patient::setSurname));
        columnDateOfBirth.setOnEditCommit(event -> handleOnEdit(event, Patient::setDateOfBirth));
        columnCareLevel.setOnEditCommit(event -> handleOnEdit(event, Patient::setCareLevel));
        columnRoomNumber.setOnEditCommit(event -> handleOnEdit(event, Patient::setRoomNumber));

        this.checkBoxShowArchived.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            this.readAllAndShowInTableView();
        });
    }

    /**
     * Handles the event of editing a patient. It updates the patient by calling the method <code>update()</code> of {@link PatientDao}.
     *
     * @param event Event including the changed object and the change.
     * @param setter Consumer to set the new value.
     */
    @FXML
    public void handleOnEdit(TableColumn.CellEditEvent<Patient, String> event, BiConsumer<Patient, String> setter) {
        var patient = event.getRowValue();

        if (patient == null)
            return;

        if( patient.isArchived()) {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Patient ist archiviert!");
            alert.setContentText("Archivierte Daten können nicht bearbeitet werden!");
            alert.showAndWait();

            // refresh the view
            this.readAllAndShowInTableView();

            return;
        }

        setter.accept(event.getRowValue(), event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * Updates a patient by calling the method <code>update()</code> of {@link PatientDao}.
     *
     * @param event Event including the changed object and the change.
     */
    private Patient doUpdate(TableColumn.CellEditEvent<Patient, String> event) {
        return this.patientDao.update(event.getRowValue());
    }

    /**
     * Reloads all patients to the table by clearing the list of all patients and filling it again by all persisted
     * patients, delivered by {@link PatientDao}.
     */
    private void readAllAndShowInTableView() {
        this.patients.clear();

        if (this.checkBoxShowArchived.isSelected())
            this.patients.addAll(this.patientDao.getAll());

        else
            this.patients.addAll(this.patientDao.getAllNotArchived());
    }

    /**
     * This method handles events fired by the button to delete patients. It calls {@link PatientDao} to delete the
     * patient from the database and removes the object from the list, which is the data source of the
     * <code>TableView</code>.
     */
    @FXML
    public void handleDelete() {
        Patient selectedItem = this.tableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null)
            return;

        if (selectedItem.isArchived())
            patientDao.restore(selectedItem.getId());
        else
            patientDao.archive(selectedItem.getId());

        readAllAndShowInTableView();
    }

    /**
     * This method handles the events fired by the button to add a patient. It collects the data from the
     * <code>TextField</code>s, creates an object of class <code>Patient</code> of it and passes the object to
     * {@link PatientDao} to persist the data.
     */
    @FXML
    public void handleAdd() {
        String firstName = this.textFieldFirstName.getText();
        String surname = this.textFieldSurname.getText();
        String birthday = this.textFieldDateOfBirth.getText();
        LocalDate birthDate = DateConverter.convertStringToLocalDate(birthday);
        String careLevel = this.textFieldCareLevel.getText();
        String roomNumber = this.textFieldRoomNumber.getText();

        // pass null for archivedOn, since new patients are not archived
        var data = new PatientCreationData(firstName, surname, birthDate, careLevel, roomNumber,  null);
        Patient patient = this.patientDao.create(data);

        patients.add(patient);

        clearTextfields();
    }

    /**
     * Clears all contents from all <code>TextField</code>s.
     */
    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldDateOfBirth.clear();
        this.textFieldCareLevel.clear();
        this.textFieldRoomNumber.clear();
    }

    /**
     * Validates the input data of the <code>TextField</code>s. It checks if the date of birth is a valid date.
     *
     * @return <code>true</code> if all input fields contain valid data, otherwise <code>false</code>.
     */
    private boolean areInputDataValid() {
        if (!this.textFieldDateOfBirth.getText().isBlank()) {
            try {
                DateConverter.convertStringToLocalDate(this.textFieldDateOfBirth.getText());
            } catch (Exception exception) {
                return false;
            }
        }

        return !this.textFieldFirstName.getText().isBlank() && !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldDateOfBirth.getText().isBlank() && !this.textFieldCareLevel.getText().isBlank() &&
                !this.textFieldRoomNumber.getText().isBlank();
    }
}
