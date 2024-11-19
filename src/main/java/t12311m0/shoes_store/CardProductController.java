package t12311m0.shoes_store;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class CardProductController {

    @FXML
    private AnchorPane card_form;

    @FXML
    private ImageView pro_ImageView;

    @FXML
    private Button pro_addBtn;

    @FXML
    private Label pro_brand;

    @FXML
    private Label pro_name;

    @FXML
    private Label pro_price;

    @FXML
    private Label pro_size;

    @FXML
    private Spinner<Integer> pro_spinner;

    private Product prodData;
    private Image image;
    private Order order;
    private Runnable refreshCallback;

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    public void setData(Product prodData) {
        this.prodData = prodData;

        // Set product information
        pro_name.setText(prodData.getName());
        pro_brand.setText(prodData.getBrandName());
        pro_price.setText("$" + prodData.getPrice());
        pro_size.setText(prodData.getSize());

        // Load product image
        String imagePath = prodData.getImageUrl();
        if (!imagePath.startsWith("file:")) {
            imagePath = "file:" + imagePath;
        }
        try {
            Image image = new Image(imagePath, 190, 94, false, true);
            pro_ImageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Failed to load image: " + e.getMessage());
            pro_ImageView.setImage(new Image("path_to_default_image"));
        }

        // Check product status
        if (prodData.getStatus() == Product.Status.UNAVAILABLE) {
            pro_addBtn.setDisable(true);
            pro_addBtn.setStyle("-fx-opacity: 0.5;"); // Dim the button
            card_form.setStyle("-fx-opacity: 0.5;");  // Dim the card
        } else {
            pro_addBtn.setDisable(false);
            pro_addBtn.setStyle("-fx-opacity: 1.0;"); // Restore button appearance
            card_form.setStyle("-fx-opacity: 1.0;"); // Restore card appearance
        }
    }
    
    @FXML
    public void initialize() {
        setSpinnerValueFactory();
    }

    private void setSpinnerValueFactory() {
        SpinnerValueFactory<Integer> spinnerValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        pro_spinner.setValueFactory(spinnerValueFactory);
    }

    @FXML
    private void addBtn() {
        int qty = pro_spinner.getValue();

        if (qty <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Quantity must be greater than zero.");
            return;
        }

        if (order == null) {
            // Create a new order with default values
            order = new Order(
                    0,  // orderId will be set after database insert
                    1,  // default customerId
                    1,  // default employeeId
                    0.0, // initial totalAmount
                    "PENDING",
                    new Date(System.currentTimeMillis())
            );
        }

        try (Connection connect = ConnectDB.connectDB()) {
            // Create new order if it doesn't exist
            if (order.getOrderId() == 0) {
                String insertOrderQuery = "INSERT INTO orders (customer_id, employee_id, total_amount, " +
                        "order_date, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

                try (PreparedStatement insertOrder = connect.prepareStatement(insertOrderQuery,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertOrder.setInt(1, order.getCustomerId());
                    insertOrder.setInt(2, order.getEmployeeId());
                    insertOrder.setDouble(3, order.getTotalAmount());
                    insertOrder.setDate(4, order.getOrderDate());
                    insertOrder.setString(5, order.getStatus());
                    insertOrder.executeUpdate();

                    try (ResultSet generatedKeys = insertOrder.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            order.setOrderId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating order failed, no ID obtained.");
                        }
                    }
                }
            }

            // Check product stock
            String checkStockQuery = "SELECT quantity, status FROM products WHERE product_id = ?";
            try (PreparedStatement prepare = connect.prepareStatement(checkStockQuery)) {
                prepare.setInt(1, prodData.getId());
                try (ResultSet result = prepare.executeQuery()) {
                    if (result.next()) {
                        int checkStock = result.getInt("quantity");
                        String status = result.getString("status");

                 if ("UNAVAILABLE".equalsIgnoreCase(status)) {
    showAlert(Alert.AlertType.ERROR, "Error Message", "Product is unavailable.");
    return;
}

if (checkStock < qty) {
    showAlert(Alert.AlertType.ERROR, "Error Message", "Not enough stock available.");
    return;
}


                        // Calculate total price for this item
                        double totalPrice = qty * prodData.getPrice();

                        // Add item to order_items
                        String insertItemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price) " +
                                "VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertItem = connect.prepareStatement(insertItemQuery)) {
                            insertItem.setInt(1, order.getOrderId());
                            insertItem.setInt(2, prodData.getId());
                            insertItem.setInt(3, qty);
                            insertItem.setDouble(4, totalPrice);
                            insertItem.executeUpdate();
                        }

                        // Update order total
                        order.setTotalAmount(order.getTotalAmount() + totalPrice);
                        String updateOrderQuery = "UPDATE orders SET total_amount = ?, updated_at = CURRENT_TIMESTAMP " +
                                "WHERE order_id = ?";
                        try (PreparedStatement updateOrder = connect.prepareStatement(updateOrderQuery)) {
                            updateOrder.setDouble(1, order.getTotalAmount());
                            updateOrder.setInt(2, order.getOrderId());
                            updateOrder.executeUpdate();
                        }

                        // Update product stock
                        int updatedStock = checkStock - qty;
                        String updateStockQuery = "UPDATE products SET quantity = ?, status = ? WHERE product_id = ?";
                        try (PreparedStatement updateStock = connect.prepareStatement(updateStockQuery)) {
                            updateStock.setInt(1, updatedStock);
                            updateStock.setString(2, updatedStock == 0 ? "UNAVAILABLE" : "AVAILABLE");
                            updateStock.setInt(3, prodData.getId());
                            updateStock.executeUpdate();
                        }

                        showAlert(Alert.AlertType.INFORMATION, "Success", "Product added to order!");

                        // Refresh the order display
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Product not found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product to order: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}