package lk.ijse.dep11.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep11.pos.db.CustomerDataAccess;
import lk.ijse.dep11.pos.db.OrderDataAccess;
import lk.ijse.dep11.pos.tm.Customer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageCustomerFormController {
    public AnchorPane root;
    public JFXTextField txtCustomerId;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerAddress;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView<Customer> tblCustomers;
    public JFXButton btnAddNew;

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }
    public void initialize(){
        String[] cols =  {"id","name","address"};
        for (int i = 0 ; i<3 ; i++) {
            tblCustomers.getColumns().get(i).setCellValueFactory(new PropertyValueFactory<>(cols[i]));
        }
        txtCustomerId.setEditable(false);
        btnDelete.setDisable(true);
        btnSave.setDefaultButton(true);
        btnAddNew.fire();
        tblCustomers.getItems().addAll(CustomerDataAccess.getAllCustomers());
        tblCustomers.getSelectionModel().selectedItemProperty().addListener((o,prev,curr) -> {
            if(curr!= null){
                txtCustomerId.setText(curr.getId());
                txtCustomerName.setText(curr.getName());
                txtCustomerAddress.setText(curr.getAddress());
                btnSave.setText("UPDATE");
                btnDelete.setDisable(false);
            }else{
                btnSave.setText("SAVE");
                btnDelete.setDisable(true);
            }
        });
        Platform.runLater(txtCustomerName::requestFocus);

    }

    public void btnAddNew_OnAction(ActionEvent actionEvent) {
        TextField[] fields = {txtCustomerId,txtCustomerName,txtCustomerAddress};
        tblCustomers.getSelectionModel().clearSelection();
        for (TextField field :fields) {
            field.clear();
        }
        if(CustomerDataAccess.getLastCustomerId()==null){
            txtCustomerId.setText("C001");
        }else{
            txtCustomerId.setText(String.format("C%03d",Integer.parseInt(CustomerDataAccess.getLastCustomerId().strip().substring(1))+1));
        }
        txtCustomerName.requestFocus();
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        if(btnSave.getText().equals("SAVE")){
            Customer newCustomer = new Customer (txtCustomerId.getText(),txtCustomerName.getText(),txtCustomerAddress.getText());
            CustomerDataAccess.saveCustomer(newCustomer);
            tblCustomers.getItems().add(newCustomer);
        } else if (btnSave.getText().equals("UPDATE")) {
            Customer updateCustomer = new Customer(txtCustomerId.getText(),txtCustomerName.getText(),txtCustomerAddress.getText());
            CustomerDataAccess.updateCustomer(updateCustomer);
            Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
            ObservableList<Customer> customerList = tblCustomers.getItems();
            customerList.set(customerList.indexOf(selectedCustomer),updateCustomer);
            tblCustomers.refresh();
        }
        btnAddNew.fire();
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
        if(OrderDataAccess.isOrderExistByCustomerId(selectedCustomer)){
            new Alert(Alert.AlertType.ERROR,"Unable to delete this customer, already associated with an order").show();

        }else{
            CustomerDataAccess.deleteCustomer(selectedCustomer.getId());
            tblCustomers.getItems().remove(selectedCustomer);
        }
        btnAddNew.fire();
    }
}
