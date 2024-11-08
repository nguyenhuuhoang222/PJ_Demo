package t12311m0.shoes_store;

import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import java.sql.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML
    private Hyperlink ad_forgot;

    @FXML
    private Button ad_login;

    @FXML
    private PasswordField ad_password;

    @FXML
    private TextField ad_username;

    @FXML
    private Button moveToStaff;

    @FXML
    private Hyperlink sf_forgot;

    @FXML
    private Button sf_login;

    @FXML
    private TextField sf_username;

    @FXML
    private PasswordField staff_password;

    @FXML
    private AnchorPane side_form;

    @FXML
    private Button moveToAdmin;

    private Connection connect;
    private ResultSet result;
    private PreparedStatement prepare;

    private Alert alert;

    public void initialize() {
        // Initialize database connection
        connect = ConnectDB.connectDB();
    }

    @FXML
    public void loginAdmin() {
        if (ad_username.getText().isEmpty() || ad_password.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both Username and Password.");
            alert.showAndWait();
        } else {
            String selectData = "SELECT admin_name, password FROM admins WHERE admin_name = ? AND password = ?";

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, ad_username.getText());
                prepare.setString(2, ad_password.getText());

                result = prepare.executeQuery();

                if (result.next()) {
                    String adminName = result.getString("admin_name");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Logged In as Admin!");
                    alert.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
                    Parent root = loader.load();

                    MainFormController mainFormController = loader.getController();
                    mainFormController.setAdminName(adminName);

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setTitle("Shoe Store Management System - Admin");
                    stage.setMinWidth(1100);
                    stage.setMinHeight(600);
                    stage.setScene(scene);
                    stage.show();

                    ad_login.getScene().getWindow().hide();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid username or password. Please try again.");
                    alert.showAndWait();
                }

            } catch (SQLException | IOException e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database or IO Error");
                alert.setHeaderText(null);
                alert.setContentText("There was a problem with the login process. Please try again later.");
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void loginEmployee() throws IOException {
        if (sf_username.getText().isEmpty() || staff_password.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both Username and Password.");
            alert.showAndWait();
        } else {
            String selectData = "SELECT employee_name, password FROM employees WHERE employee_name = ? AND password = ?";

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, sf_username.getText());
                prepare.setString(2, staff_password.getText());

                result = prepare.executeQuery();

                if (result.next()) {
                    // Display login success message
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Logged In as Employee!");
                    alert.showAndWait();

                    // Load MainFormEmployee.fxml for Employee interface
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFormEmployee.fxml"));
                    Parent root = loader.load();

                    // Obtain controller for employee main form to set data if needed
                    MainFormEmployeeController mainFormEmployeeController = loader.getController();
                    mainFormEmployeeController.setEmployeeName(result.getString("employee_name"));

                    // Setup new stage and scene for employee main form
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setTitle("Shoe Store Management System - Employee");
                    stage.setMinWidth(900);
                    stage.setMinHeight(500);
                    stage.setScene(scene);
                    stage.show();

                    // Hide login window
                    sf_login.getScene().getWindow().hide();

                } else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Invalid Employee Username or Password. Please try again.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("There was a problem with the login process. Please try again later.");
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void switchToSecondary(ActionEvent event) throws IOException {
        TranslateTransition slider = new TranslateTransition();
        if (event.getSource() == moveToStaff) {
            slider.setNode(side_form);
            slider.setToX(300);
            slider.setDuration(Duration.seconds(0.5));
            slider.setOnFinished((ActionEvent e) -> {
                moveToAdmin.setVisible(true);
                moveToStaff.setVisible(false);
            });
            slider.play();
        } else if (event.getSource() == moveToAdmin) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(0.5));
            slider.setOnFinished((ActionEvent e) -> {
                moveToAdmin.setVisible(false);
                moveToStaff.setVisible(true);
            });
            slider.play();
        }
    }
}
