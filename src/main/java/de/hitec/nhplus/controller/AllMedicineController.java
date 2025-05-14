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

/**
 * The <code>AllMedicineController</code> contains the entire logic of the medicine view. It determines which data is displayed and how to react to events.
 */
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

    /**
     * Initializes the controller class. It gets all medicines from the database and displays them in the table view.
     */
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

    /**
     * Validates the input data of the <code>TextField</code>s. It checks if the expiration date is a valid date.
     * @return <code>true</code> if all input fields contain valid data, otherwise <code>false</code>.
     */
    private boolean areInputDataValid() {
        try {
            DateConverter.convertStringToLocalDate(this.textFieldExpirationDate.getText());
        } catch (Exception exception) {
            return false;
        }

        return !this.textFieldName.getText().isBlank() &&
                !this.textFieldStorage.getText().isBlank();
    }

    /**
     * Reloads all medicines to the table by clearing the list of all medicines and filling it again by all persisted
     * medicines, delivered by {@link MedicineDao}.
     */
    private void loadMedicines() {
        this.medicines.clear();
        this.dao = DaoFactory.getDaoFactory().createMedicineDAO();
        try {
            this.medicines.addAll(this.dao.readAll());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Handles the event of adding a new medicine. It collects the data from the text fields and passes it to the
     * {@link MedicineDao} to persist the data.
     *
     * @param actionEvent Event including the clicked button.
     */
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

    /**
     * Handles the event of deleting a medicine. It deletes the medicine from the database and removes it from the
     * list, which is the data source of the <code>TableView</code>.
     *
     * @param actionEvent Event including the clicked button.
     */
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

    /**
     * Clears all contents from all <code>TextField</code>'s.
     */
    private void clearTextFields() {
        this.textFieldName.clear();
        this.textFieldStorage.clear();
        this.textFieldExpirationDate.clear();
    }
}
