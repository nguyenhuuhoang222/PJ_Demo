/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package t12311m0.shoes_store;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;


/**
 * FXML Controller class
 *
 * @author admin
 */
public class MainFormEmployeeController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Label empName;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
        public void setEmployeeName(String employeeName) {
        empName.setText("Welcome, " + employeeName + "!");
    }
    
}
