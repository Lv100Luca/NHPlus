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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class AllCaregiverController {

    @FXML
    private TableView<Caregiver> tableView;
    @FXML
    private TableColumn<Caregiver, Integer> columnId;
    @FXML
    private TableColumn<Caregiver, String> columnFirstName;
    @FXML
    private TableColumn<Caregiver, String> columnSurname;
    @FXML
    private TableColumn<Caregiver, String> columnPhoneNumber;

    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldSurname;
    @FXML
    private TextField textFieldPhoneNumber;

    @FXML
    private CheckBox checkBoxShowArchived;

    private final ObservableList<Caregiver> caregivers = FXCollections.observableArrayList();
    private CaregiverDao caregiverDao;

    public void initialize() {
        caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();

        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("cid"));
        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        this.columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        this.columnPhoneNumber.setCellFactory(TextFieldTableCell.forTableColumn());

        this.tableView.setItems(this.caregivers);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, caregiver, t1) -> AllCaregiverController.this.buttonDelete.setDisable(t1 == null));

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewCaregiverListener = (observableValue, oldValue, newValue) ->
                AllCaregiverController.this.buttonAdd.setDisable(!AllCaregiverController.this.areInputDataValid());
        this.textFieldFirstName.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldSurname.textProperty().addListener(inputNewCaregiverListener);
        this.textFieldPhoneNumber.textProperty().addListener(inputNewCaregiverListener);

        this.checkBoxShowArchived.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            this.readAllAndShowInTableView();
        });
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
        var selectedIndex = this.tableView.getSelectionModel().getSelectedIndex();
        var deleted = caregiverDao.delete(selectedIndex);

        deleted.ifPresent(this.caregivers::remove);
    }

    @FXML
    private void handleAdd() {
        String firstName = this.textFieldFirstName.getText();
        String surname = this.textFieldSurname.getText();
        String phoneNumber = this.textFieldPhoneNumber.getText();
        this.caregiverDao.create(new CaregiverCreationData(firstName, surname, phoneNumber, null));

        this.readAllAndShowInTableView();
        this.clearTextFields();
    }

    private void clearTextFields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldPhoneNumber.clear();
    }

    private void doUpdate(TableColumn.CellEditEvent<Caregiver, String> event) {
        this.caregiverDao.update(event.getRowValue());
    }

    private boolean areInputDataValid() {
        return PhoneNumberUtil.isValidPhoneNumber(this.textFieldPhoneNumber.getText())
                && !this.textFieldFirstName.getText().isEmpty()
                && !this.textFieldSurname.getText().isEmpty();
    }

    private void readAllAndShowInTableView() {
        this.caregivers.clear();

        if (this.checkBoxShowArchived.isSelected())
            this.caregivers.addAll(this.caregiverDao.getAll());

        else
            this.caregivers.addAll(this.caregiverDao.getAllNotArchived());
    }

}
