package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.Caregiver;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;

import java.sql.SQLException;
import java.time.LocalDate;

public class TreatmentController {

    @FXML
    private Label labelPatientName;

    @FXML
    private Label labelCareLevel;

    @FXML
    public Label labelCaregiver;

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
    private Patient patient;
    private Treatment treatment;

    public void initializeController(AllTreatmentController controller, Stage stage, Treatment treatment) {
        this.stage = stage;
        this.controller= controller;
        PatientDao pDao = DaoFactory.getDaoFactory().createPatientDAO();

        var patient = pDao.getById(treatment.getPid());

        if (patient.isEmpty())
            throw new RuntimeException("Patient not found");

        this.patient = patient.get();

        this.treatment = treatment;
        showData();
    }

    private void showData() {
        Caregiver cg = getCaregiver();
        this.labelPatientName.setText(patient.getSurname()+", "+patient.getFirstName());
        this.labelCareLevel.setText(patient.getCareLevel());
        if (cg != null) {
            this.labelCaregiver.setText(cg.getSurname()+", "+cg.getFirstName());
        } else {
            this.labelCaregiver.setText("Keine Pflegekraft zugewiesen");
        }
        LocalDate date = DateConverter.convertStringToLocalDate(treatment.getDate());
        this.datePicker.setValue(date);
        this.textFieldBegin.setText(this.treatment.getBegin());
        this.textFieldEnd.setText(this.treatment.getEnd());
        this.textFieldDescription.setText(this.treatment.getDescription());
        this.textAreaRemarks.setText(this.treatment.getRemarks());
    }

    private Caregiver getCaregiver(){
        CaregiverDao caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
        Caregiver caregiver = null;
        try {
            caregiver = caregiverDao.read((int) treatment.getCid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return caregiver;
    }

    @FXML
    public void handleChange(){
        this.treatment.setDate(this.datePicker.getValue().toString());
        this.treatment.setBegin(textFieldBegin.getText());
        this.treatment.setEnd(textFieldEnd.getText());
        this.treatment.setDescription(textFieldDescription.getText());
        this.treatment.setRemarks(textAreaRemarks.getText());
        doUpdate();
        controller.readAllAndShowInTableView();
        stage.close();
    }

    private void doUpdate(){
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();

        dao.update(treatment);
    }

    @FXML
    public void handleCancel(){
        stage.close();
    }
}