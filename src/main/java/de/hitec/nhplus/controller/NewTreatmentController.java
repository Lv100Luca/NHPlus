package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.CreationData.TreatmentCreationData;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Medicine;
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

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class NewTreatmentController {

    @FXML
    private Label labelFirstName;

    @FXML
    private Label labelSurname;

    @FXML
    private ComboBox<Medicine> comboBoxMedicine;

    private final ObservableList<Medicine> medications = FXCollections.observableArrayList();

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

    private TreatmentDao treatmentDao;
    private CaregiverDao caregiverDao;
    private PatientDao patientDao;
    private MedicineDao medicineDao;

    public void initialize(AllTreatmentController controller, Stage stage, Patient patient) {
        treatmentDao = DaoFactory.getDaoFactory().createTreatmentDao();
        caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
        patientDao = DaoFactory.getDaoFactory().createPatientDAO();
        medicineDao = DaoFactory.getDaoFactory().createMedicineDAO();

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
        this.setComboBoxData();
        this.createComboBoxData();
        this.showPatientMedicine();
    }

    private void createComboBoxData() {
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

    private void showPatientMedicine() {
        this.labelFirstName.setText(patient.getFirstName());
        this.labelSurname.setText(patient.getSurname());
    }

    @FXML
    public void handleAdd() {
        LocalDate date = this.datePicker.getValue();
        LocalTime begin = DateConverter.convertStringToLocalTime(textFieldBegin.getText());
        LocalTime end = DateConverter.convertStringToLocalTime(textFieldEnd.getText());
        String description = textFieldDescription.getText();
        String remarks = textAreaRemarks.getText();
        Caregiver caregiver = comboBoxCaregiver.getSelectionModel().getSelectedItem();
        Medicine medicine = comboBoxMedicine.getSelectionModel().getSelectedItem();

        var data = new TreatmentCreationData(patient.getId(), date, begin, end, description, remarks, caregiver.getId(), medicine.getId());
        Treatment treatment = treatmentDao.create(data);

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

    /**
     * Reloads all medicines to the combo box by clearing the list of all medicines and filling it again by all
     * persisted medicines, delivered by {@link MedicineDao}.
     */
    private void setComboBoxData() {
        this.medications.addAll(medicineDao.getAll());

        this.comboBoxMedicine.setItems(this.medications);
        this.comboBoxMedicine.setConverter(new StringConverter<>() {
            @Override
            public String toString(Medicine medicine) {
                if (medicine == null)
                    return "";

                return medicine.getName();
            }

            @Override
            public Medicine fromString(String name) {
                return comboBoxMedicine.getSelectionModel().getSelectedItem();
            }
        });
    }
}