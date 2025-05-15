package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.model.CreationData.CaregiverCreationData;
import de.hitec.nhplus.utils.PhoneNumberUtil;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;

public class AllCaregiverController {

    @FXML
    private TableView<Caregiver> tableView;
    @FXML
    private TableColumn<Caregiver, Integer> columnId;
    @FXML
    private TableColumn<Caregiver, String> columnFirstName;
    @FXML
    private TableColumn<Caregiver, String> columnsurname;
    @FXML
    private TableColumn<Caregiver, String> columnPhoneNumber;

    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldsurname;
    @FXML
    private TextField textFieldPhoneNumber;

    private final ObservableList<Caregiver> caregivers = FXCollections.observableArrayList();
    private CaregiverDao caregiverDao;

    public void initialize() {
        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("cid"));
        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        this.columnsurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnsurname.setCellFactory(TextFieldTableCell.forTableColumn());
        this.columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        this.columnPhoneNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        this.tableView.setItems(this.caregivers);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, caregiver, t1) -> AllCaregiverController.this.buttonDelete.setDisable(t1 == null));

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewCaregiverListener = (observableValue, oldValue, newValue) ->
                AllCaregiverController.this.buttonAdd.setDisable(!AllCaregiverController.this.areInputDataValid());
        this.textFieldFirstName.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldsurname.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldPhoneNumber.textProperty().addListener(inputNewCaregiverListener);
    }

    @FXML
    private void handleOnEditFirstName(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    private void handleOnEditsurname(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    private void handleOnEditPhoneNumber(TableColumn.CellEditEvent<Caregiver, String> event) {
        event.getRowValue().setPhoneNumber(event.getNewValue());
        this.doUpdate(event);
    }

    @FXML
    private void handleDelete() {
        var doa = DaoFactory.getDaoFactory().createCaregiverDAO();

        var selectedIndex = this.tableView.getSelectionModel().getSelectedIndex();
        var deleted = doa.delete(selectedIndex);

        deleted.ifPresent(this.caregivers::remove);
    }

    @FXML
    private void handleAdd() {
        String firstName = this.textFieldFirstName.getText();
        String surname = this.textFieldsurname.getText();
        String phoneNumber = this.textFieldPhoneNumber.getText();
        this.caregiverDao.create(new CaregiverCreationData(firstName, surname, phoneNumber));

        this.readAllAndShowInTableView();
        this.clearTextFields();
    }

    private void clearTextFields() {
        this.textFieldFirstName.clear();
        this.textFieldsurname.clear();
        this.textFieldPhoneNumber.clear();
    }

    private void doUpdate(TableColumn.CellEditEvent<Caregiver, String> event) {
        this.caregiverDao.update(event.getRowValue());
    }

    private boolean areInputDataValid() {
        return PhoneNumberUtil.isValidPhoneNumber(this.textFieldPhoneNumber.getText())
                && !this.textFieldFirstName.getText().isEmpty()
                && !this.textFieldsurname.getText().isEmpty();
    }

    private void readAllAndShowInTableView() {
        this.caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();

        this.caregivers.clear();

        this.caregivers.addAll(this.caregiverDao.getAll());
    }

}
