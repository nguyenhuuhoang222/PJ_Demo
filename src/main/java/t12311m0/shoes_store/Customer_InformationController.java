package t12311m0.shoes_store;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class Customer_InformationController implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private Label customerEmail;

    @FXML
    private Label customerGender;

    @FXML
    private Label customerName;

    @FXML
    private Label customerOrderCount;

    @FXML
    private Label customerPhone;

    @FXML
    private TableView<Order> orderTable;  // Declare TableView for orders

    @FXML
    private TableColumn<Order, Integer> oderid_Infor;  // Correct column type for orderId

    @FXML
    private TableColumn<Order, String> status_Infor;  // Correct column type for status

    @FXML
    private TableColumn<Order, Double> total_Infor;  // Correct column type for totalAmount

    private ObservableList<Order> orders = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configure table columns
        oderid_Infor.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        status_Infor.setCellValueFactory(new PropertyValueFactory<>("status"));
        total_Infor.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // Add event to the back button
        backButton.setOnAction(e -> handleBackButton());
    }

public void setCustomerData(Customer customer, List<Order> customerOrders) {
    // Update customer information
    customerName.setText(customer.getCustomerName());
    customerEmail.setText(customer.getEmail());
    customerPhone.setText(customer.getPhone());
    customerGender.setText(customer.getGender());
    customerOrderCount.setText(String.valueOf(customerOrders.size()));  // Display count of orders

    // Populate order data into the table
    orders.setAll(customerOrders);  // Set the list of orders in the ObservableList
    orderTable.setItems(orders);  // Bind the table to the observable list of orders
}

private void handleBackButton() {
        // Close the current window
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    // Optional: If you need to fetch orders from the database
// Method to fetch orders by customer ID
private List<Order> fetchOrdersByCustomerId(int customerId) {
    List<Order> orders = new ArrayList<>();
    String query = "SELECT order_id, customer_id, employee_id, total_amount, status, order_date, created_at, updated_at " +
                   "FROM orders WHERE customer_id = ?";

    try (Connection connect = ConnectDB.connectDB(); PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int orderId = rs.getInt("order_id");
            int employeeId = rs.getInt("employee_id");
            double totalAmount = rs.getDouble("total_amount");
            String status = rs.getString("status");
            Date orderDate = rs.getDate("order_date");
            Date createdAt = rs.getDate("created_at");
            Date updatedAt = rs.getDate("updated_at");

            Order order = new Order(orderId, customerId, employeeId, totalAmount, status, orderDate, createdAt, updatedAt);
            orders.add(order);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return orders;
}

  
}
    