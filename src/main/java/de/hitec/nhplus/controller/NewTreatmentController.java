package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.CreationData.TreatmentCreationData;
import de.hitec.nhplus.model.Caregiver;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class NewTreatmentController {

    @FXML
    private Label labelFirstName;

    @FXML
    private Label labelSurname;

    @FXML
    private TextField textFieldBegin;

    @FXML
    private TextField textFieldEnd;

    @FXML
    private TextField textFieldDescription;

    @FXML
    private TextArea textAreaRemarks;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Caregiver> comboBoxCaregiver;

    private final ObservableList<Caregiver> caregiverSelection = FXCollections.observableArrayList();

    @FXML
    private Button buttonAdd;

    private AllTreatmentController controller;
    private Patient patient;
    private Stage stage;

    public void initialize(AllTreatmentController controller, Stage stage, Patient patient) {
        this.controller = controller;
        this.patient = patient;
        this.stage = stage;

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewPatientListener = (observableValue, oldText, newText) ->
                NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid());
        this.textFieldBegin.textProperty().addListener(inputNewPatientListener);
        this.textFieldEnd.textProperty().addListener(inputNewPatientListener);
        this.textFieldDescription.textProperty().addListener(inputNewPatientListener);
        this.textAreaRemarks.textProperty().addListener(inputNewPatientListener);
        this.datePicker.valueProperty().addListener((observableValue, localDate, t1) -> NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid()));
        this.datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                return (localDate == null) ? "" : DateConverter.convertLocalDateToString(localDate);
            }

            @Override
            public LocalDate fromString(String localDate) {
                return DateConverter.convertStringToLocalDate(localDate);
            }
        });
        this.createComboBoxData();
        this.showPatientData();
    }

    private void createComboBoxData() {
        CaregiverDao caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();

        this.caregiverSelection.addAll(caregiverDao.getAll());

        this.comboBoxCaregiver.setItems(this.caregiverSelection);
        this.comboBoxCaregiver.setConverter(new StringConverter<>() {
            @Override
            public String toString(Caregiver caregiver) {
                if (caregiver == null)
                    return " - ";

                return caregiver.getFirstName() + " " + caregiver.getSurname();
            }

            @Override
            public Caregiver fromString(String string) {
                return comboBoxCaregiver.getSelectionModel().getSelectedItem();
            }
        });
    }

    private void showPatientData() {
        this.labelFirstName.setText(patient.getFirstName());
        this.labelSurname.setText(patient.getSurname());
    }

    @FXML
    public void handleAdd() {
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        CaregiverDao caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();

        LocalDate date = this.datePicker.getValue();
        LocalTime begin = DateConverter.convertStringToLocalTime(textFieldBegin.getText());
        LocalTime end = DateConverter.convertStringToLocalTime(textFieldEnd.getText());
        String description = textFieldDescription.getText();
        String remarks = textAreaRemarks.getText();
        Caregiver caregiver = comboBoxCaregiver.getSelectionModel().getSelectedItem();

        var data = new TreatmentCreationData(patient, date, begin, end, description, remarks, caregiver);
        Treatment treatment = dao.create(data);

        controller.readAllAndShowInTableView();
        stage.close();
    }

    @FXML
    public void handleCancel() {
        stage.close();
    }

    private boolean areInputDataInvalid() {
        if (this.textFieldBegin.getText() == null || this.textFieldEnd.getText() == null) {
            return true;
        }
        try {
            LocalTime begin = DateConverter.convertStringToLocalTime(this.textFieldBegin.getText());
            LocalTime end = DateConverter.convertStringToLocalTime(this.textFieldEnd.getText());
            if (!end.isAfter(begin)) {
                return true;
            }
        } catch (Exception exception) {
            return true;
        }
        return this.textFieldDescription.getText().isBlank() || this.datePicker.getValue() == null;
    }
}