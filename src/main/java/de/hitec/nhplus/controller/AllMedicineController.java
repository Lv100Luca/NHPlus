package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.MedicineDao;
import de.hitec.nhplus.model.Medicine;
import de.hitec.nhplus.utils.DateConverter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AllMedicineController {

    @FXML
    private TableView<Medicine> tableView;

    @FXML
    private TableColumn<Medicine, Integer> columnId;

    @FXML
    private TableColumn<Medicine, String> columnName;

    @FXML
    private TableColumn<Medicine, String> columnStorage;

    @FXML
    private TableColumn<Medicine, String> columnExpirationDate;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldStorage;

    @FXML
    private TextField textFieldExpirationDate;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button buttonDelete;

    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();
    private MedicineDao dao;

    public void initialize() {
        loadMedicines();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("mid"));
        this.columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.columnStorage.setCellValueFactory(new PropertyValueFactory<>("storage"));
        this.columnExpirationDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        this.tableView.setItems(this.medicines);

        this.buttonDelete.setDisable(true);
        // disable the button, if nothing is selected
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Medicine>() {
            @Override
            public void changed(ObservableValue<? extends Medicine> observableValue, Medicine oldMedicine, Medicine newMedicine) {
                AllMedicineController.this.buttonDelete.setDisable(newMedicine == null);
            }
        });

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewMedicineListener = (observableValue, oldText, newText) ->
                AllMedicineController.this.buttonAdd.setDisable(!AllMedicineController.this.areInputDataValid());
        this.textFieldName.textProperty().addListener(inputNewMedicineListener);
        this.textFieldStorage.textProperty().addListener(inputNewMedicineListener);
        this.textFieldExpirationDate.textProperty().addListener(inputNewMedicineListener);
    }

    private boolean areInputDataValid() {
        try {
            DateConverter.convertStringToLocalDate(this.textFieldExpirationDate.getText());
        } catch (Exception exception) {
            return false;
        }

        return !this.textFieldName.getText().isBlank() &&
                !this.textFieldStorage.getText().isBlank();
    }

    private void loadMedicines() {
        this.medicines.clear();
        this.dao = DaoFactory.getDaoFactory().createMedicineDAO();
        try {
            this.medicines.addAll(this.dao.readAll());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void handleAdd(ActionEvent actionEvent) {
        String name = this.textFieldName.getText();
        String storage = this.textFieldStorage.getText();
        String expirationDate = this.textFieldExpirationDate.getText();
        try {
            this.dao.create(new Medicine(name, storage, expirationDate));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        loadMedicines();
        clearTextFields();
    }

    public void handleDelete(ActionEvent actionEvent) {
        Medicine selectedItem = this.tableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null)
            return;

        try {
            this.dao.deleteById(selectedItem.getMid());
            this.tableView.getItems().remove(selectedItem);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void clearTextFields() {
        this.textFieldName.clear();
        this.textFieldStorage.clear();
        this.textFieldExpirationDate.clear();
    }
}
