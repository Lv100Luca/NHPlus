package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.*;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.Medicine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * The <code>TreatmentController</code> contains the entire logic of the treatment view. It determines which data is displayed and how to react to events.
 */
public class TreatmentController {

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelPatientName;

    @FXML
    private Label labelCareLevel;

    @FXML
    private Label labelCaregiver;

    @FXML
    private Label labelMedicine;

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

    private AllTreatmentController controller;
    private Stage stage;

    private TreatmentDao treatmentDao;
    private CaregiverDao caregiverDao;
    private PatientDao patientDao;
    private MedicineDao medicineDao;

    private Patient patient;
    private Treatment treatment;

    /**
     * Initializes the controller class. It gets all medicines from the database and displays them in the combo box.
     *
     * @param controller The controller of the <code>AllTreatmentView</code>.
     * @param stage The stage of the <code>AllTreatmentView</code>.
     * @param treatment The treatment for which the treatment window should be opened.
     */
    public void initializeController(AllTreatmentController controller, Stage stage, Treatment treatment) {
        treatmentDao = DaoFactory.getDaoFactory().createTreatmentDao();
        caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
        patientDao = DaoFactory.getDaoFactory().createPatientDAO();
        medicineDao = DaoFactory.getDaoFactory().createMedicineDAO();

        this.stage = stage;
        this.controller = controller;
        this.treatment = treatment;

        var patient = patientDao.getById(treatment.getPid());

        if (patient.isEmpty()) {
            handleCancel();
            return;
        }

        this.patient = patient.get();

        showData();

        if (this.treatment.isArchived()) {
            this.labelTitle.setText("Archivierte Behandlung");

            this.textFieldBegin.setDisable(true);
            this.textFieldEnd.setDisable(true);
            this.textFieldDescription.setDisable(true);
            this.textAreaRemarks.setDisable(true);
            this.datePicker.setDisable(true);
        }
    }

    /**
     * Loads the patient's data into the labels.
     */
    private void showData() {
        LocalDate date = DateConverter.convertStringToLocalDate(treatment.getDate());

        this.labelPatientName.setText(patient.getSurname() + ", " + patient.getFirstName());
        this.labelCareLevel.setText(patient.getCareLevel());
        this.datePicker.setValue(date);
        this.textFieldBegin.setText(this.treatment.getBegin());
        this.textFieldEnd.setText(this.treatment.getEnd());
        this.textFieldDescription.setText(this.treatment.getDescription());
        this.textAreaRemarks.setText(this.treatment.getRemarks());

        var caregiverName = caregiverDao.getById(treatment.getCid())
                .map(Caregiver::getFullName)
                .orElse(" - ");

        var medicineName = medicineDao.getById(treatment.getMid())
                .map(Medicine::getName)
                .orElse(" - ");

        this.labelCaregiver.setText(caregiverName);
        this.labelMedicine.setText(medicineName);
    }

    /**
     * Handles the event of changing the treatment.
     * It updates the treatment by calling the method <code>update()</code> of {@link TreatmentDao}.
     */
    @FXML
    public void handleChange() {
        this.treatment.setDate(this.datePicker.getValue().toString());
        this.treatment.setBegin(textFieldBegin.getText());
        this.treatment.setEnd(textFieldEnd.getText());
        this.treatment.setDescription(textFieldDescription.getText());
        this.treatment.setRemarks(textAreaRemarks.getText());

        treatmentDao.update(treatment);

        controller.readAllAndShowInTableView();
        stage.close();
    }

    /**
     * Handles the event of closing the window.
     */
    @FXML
    public void handleCancel() {
        stage.close();
    }
}