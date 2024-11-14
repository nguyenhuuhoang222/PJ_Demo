package t12311m0.shoes_store;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class SalesPageController {

    public SalesPageController() {

    }

    @FXML
    private Button Receiptbtn;

    @FXML
    private Button Removebtn;

    @FXML
    private TextField amount;

    @FXML
    private Label change;

    @FXML
    private Label empName;

    @FXML
    private Button logoutButton;

    @FXML
    private Button m_e_btn;

    @FXML
    private Button m_f_btn;

    @FXML
    private GridPane menu_GridPane;

    @FXML
    private ScrollPane menu_ScrollPane;

    @FXML
    private Button payBtn;

    @FXML
    private Button r_r_btn;

    @FXML
    private Label total;
    @FXML
    private TableView<OrderItem> tableView;

    @FXML
    private TableColumn<OrderItem, String> col_ProductName;

    @FXML
    private TableColumn<OrderItem, Integer> col_Quantity;

    @FXML
    private TableColumn<OrderItem, Double> col_Price;

    private Connection connect;
    private ResultSet result;
    private PreparedStatement prepare;
    private final List<Product> products = new ArrayList<>();
    private int employeeId;
    private Order currentOrder;

    public SalesPageController(int getid) {
        this.getid = getid;
    }

    public void initData(String employeeName, int employeeId) {
        setEmployeeName(employeeName);
        this.employeeId = employeeId;
    }

    @FXML
    public void logout() {
        if (confirmLogout()) {
            closeCurrentWindow();
            loadLoginForm();
        }
    }

    private void loadLoginForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Shoes Store Management System");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login form: " + e.getMessage());
        }
    }

    public boolean confirmLogout() {
        Optional<ButtonType> result = showConfirmationDialog("Logout Confirmation", "Are you sure you want to logout?");
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private Optional<ButtonType> showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void closeCurrentWindow() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }

    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();

    public void initialize() {
        try {
            if (currentOrder == null) {
                currentOrder = new Order(
                        0,
                        1,
                        1,
                        0.0,
                        "PENDING",
                        new java.sql.Date(System.currentTimeMillis())
                );
            }

            setupTableView();
            menuDisplayCard();
            displayTotal();

            orderItems.addListener((javafx.collections.ListChangeListener.Change<? extends OrderItem> c) -> {
                displayTotal();
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize the sales page.");
        }
    }

    private void setupTableView() {
        col_ProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        col_Quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        col_Price.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.setItems(orderItems);
    }

    public void setEmployeeName(String employeeName) {
        empName.setText("Welcome, " + employeeName + "!");
    }

    private ObservableList<Product> cardListData = FXCollections.observableArrayList();

    public ObservableList<Product> menuGetData() {
        String sql = "SELECT * FROM products";
        ObservableList<Product> listData = FXCollections.observableArrayList();

        try (Connection connect = ConnectDB.connectDB(); PreparedStatement prepare = connect.prepareStatement(sql); ResultSet result = prepare.executeQuery()) {

            while (result.next()) {
                int productId = result.getInt("product_id");
                String productName = result.getString("product_name");
                int brandId = result.getInt("brand_id");
                String brandName = Product.getBrandNameById(brandId);
                double price = result.getDouble("price");
                String size = result.getString("product_size");
                String imageUrl = result.getString("image_url");

                Product prod = new Product(productId, productName, brandName, price, size, imageUrl);
                listData.add(prod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listData;
    }

public void refreshOrderDisplay() {
    try (Connection connect = ConnectDB.connectDB()) {
        if (currentOrder != null) {
            String query = "SELECT oi.order_item_id, oi.order_id, oi.product_id, p.product_name, oi.quantity, oi.price "
                    + "FROM order_items oi "
                    + "JOIN products p ON oi.product_id = p.product_id "
                    + "WHERE oi.order_id = ?";

            try (PreparedStatement stmt = connect.prepareStatement(query)) {
                stmt.setInt(1, currentOrder.getOrderId());
                ResultSet rs = stmt.executeQuery();

                orderItems.clear();
                double totalAmount = 0.0;

                while (rs.next()) {
                    int orderItemId = rs.getInt("order_item_id");
                    int orderId = rs.getInt("order_id");
                    int productId = rs.getInt("product_id"); // Sửa thành "product_id"
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");

                    OrderItem item = new OrderItem(orderItemId, orderId, productId, productName, quantity, price);
                    orderItems.add(item);
                    totalAmount += price * quantity; // Tính tổng bằng cách nhân giá với số lượng
                }

                final double finalTotal = totalAmount;
                javafx.application.Platform.runLater(() -> {
                    total.setText(String.format("%.2f$", finalTotal));
                    tableView.refresh(); // Làm mới TableView
                });

                updateOrderTotal(currentOrder.getOrderId(), totalAmount);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to refresh order display: " + e.getMessage());
    }
}

    private void updateOrderTotal(int orderId, double totalAmount) {
        try (Connection connect = ConnectDB.connectDB()) {
            String updateQuery = "UPDATE orders SET total_amount = ? WHERE order_id = ?";
            try (PreparedStatement stmt = connect.prepareStatement(updateQuery)) {
                stmt.setDouble(1, totalAmount);
                stmt.setInt(2, orderId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void menuDisplayCard() throws SQLException {
        cardListData.clear();
        cardListData.addAll(menuGetData());

        menu_GridPane.getChildren().clear();
        menu_GridPane.getRowConstraints().clear();
        menu_GridPane.getColumnConstraints().clear();

        int row = 0;
        int column = 0;

        for (int q = 0; q < cardListData.size(); q++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CardProduct.fxml"));
                AnchorPane cardProductPane = loader.load();

                CardProductController controller = loader.getController();
                controller.setData(cardListData.get(q));
                controller.setOrder(currentOrder);

                controller.setRefreshCallback(this::refreshOrderDisplay);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                menu_GridPane.add(cardProductPane, column++, row);
                GridPane.setMargin(cardProductPane, new Insets(10));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        configureGrid();
    }

    private void configureGrid() {
        for (int i = 0; i < 3; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setHgrow(Priority.ALWAYS);
            menu_GridPane.getColumnConstraints().add(colConst);
        }

        int numberOfRows = (int) Math.ceil(cardListData.size() / 3.0);
        for (int i = 0; i < numberOfRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setVgrow(Priority.ALWAYS);
            menu_GridPane.getRowConstraints().add(rowConst);
        }
    }

    public int getCustomerId(String customerName, String phone) {
        try (Connection conn = ConnectDB.connectDB(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT customer_id FROM customers WHERE customer_name = ? AND phone = ?")) {
            stmt.setString(1, customerName);
            stmt.setString(2, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            } else {
                showCustomerInfoDialog(customerName, phone);
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showCustomerInfoDialog(String customerName, String phone) {
        TextInputDialog dialog = new TextInputDialog("Customer");
        dialog.setTitle("Customer Information");
        dialog.setHeaderText("Customer Information Required");
        dialog.setContentText("Please enter the customer's gender:");

        String email = "customer@gmail.com";
        String defaultPhone = "00000000000";

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String gender = result.get().trim();
            if (!gender.isEmpty()) {
                createCustomer(customerName, defaultPhone, email, gender);
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Gender is required.");
            }
        }
    }

    private void createCustomer(String name, String phone, String email, String gender) {
        try (Connection conn = ConnectDB.connectDB(); PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO customers (customer_name, phone, email, gender) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, name);
            insertStmt.setString(2, phone);
            insertStmt.setString(3, email);
            insertStmt.setString(4, gender);
            insertStmt.executeUpdate();
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newCustomerId = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public int createOrder(int customerId, int employeeId, double totalAmount) {
        try (Connection conn = ConnectDB.connectDB(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO orders (customer_id, employee_id, total_amount, status) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, employeeId);
            stmt.setDouble(3, totalAmount);
            stmt.setString(4, "Processing");
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void addOrderItem(int orderId, int productId, int quantity, double price) {
        try (Connection conn = ConnectDB.connectDB(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void processOrder(String customerName, String phone, int employeeId, List<Product> products) {
        int customerId = getCustomerId(customerName, phone);
        if (customerId == -1) {
            System.out.println("Failed to find or create customer.");
            return;
        }

        double totalAmount = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        int orderId = createOrder(customerId, employeeId, totalAmount);
        if (orderId == -1) {
            System.out.println("Failed to create order.");
            return;
        }

        for (Product product : products) {
            addOrderItem(orderId, product.getId(), product.getQuantity(), product.getPrice());
        }

        System.out.println("Order processed successfully.");
    }

    public Order getOrder(int orderId) {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection connect = ConnectDB.connectDB(); PreparedStatement prepare = connect.prepareStatement(sql)) {
            prepare.setInt(1, orderId);
            try (ResultSet result = prepare.executeQuery()) {
                if (result.next()) {
                    int customerId = result.getInt("customer_id");
                    int employeeId = result.getInt("employee_id");
                    double totalAmount = result.getDouble("total_amount");
                    Date orderDate = result.getDate("order_date");
                    String status = result.getString("status");
                    order = new Order(orderId, customerId, employeeId, totalAmount, status, orderDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public void menuRestart() {
        cardListData.clear();
        try {
            menuDisplayCard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

@FXML

private void handlePay(ActionEvent event) {
    if (orderItems.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Empty Order", "Please add items to the order before payment.");
        return;
    }

    // Lấy tổng số tiền từ giao diện và loại bỏ ký hiệu `$`
    double totalCost;
    try {
        totalCost = Double.parseDouble(total.getText().replace("$", ""));
        if (totalCost <= 0) {
            showAlert(Alert.AlertType.WARNING, "Invalid Order", "Order total must be greater than zero.");
            return;
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Invalid Order Total", "The order total is not a valid number.");
        return;
    }

    // Kiểm tra đầu vào của số tiền thanh toán
    double amountPaid;
    try {
        if (amount.getText().isEmpty()) {
            throw new NumberFormatException("Empty payment field");
        }
        amountPaid = Double.parseDouble(amount.getText());
        if (amountPaid <= 0) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Payment amount must be greater than zero.");
            return;
        }
        if (amountPaid < totalCost) {
            showAlert(Alert.AlertType.ERROR, "Insufficient Payment",
                    "The payment amount ($" + String.format("%.2f", amountPaid) +
                    ") is less than the total cost ($" + String.format("%.2f", totalCost) + ")");
            return;
        }
    } catch (NumberFormatException e) {
        showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the payment amount.");
        amount.clear();
        amount.requestFocus();
        return;
    }

    // Tính toán tiền thối lại và hiển thị
    double changeAmount = amountPaid - totalCost;
    change.setText(String.format("%.2f$", changeAmount));

    // Xác nhận đơn hàng hiện tại và cập nhật trạng thái
    if (currentOrder != null) {
        try (Connection conn = ConnectDB.connectDB()) {
            String updateQuery = "UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, "COMPLETED");
                stmt.setInt(2, currentOrder.getOrderId());
                stmt.executeUpdate();
            }

            // Cập nhật trạng thái đơn hàng trong đối tượng `currentOrder`
            currentOrder.setStatus("COMPLETED");

            // Hiển thị thông báo thanh toán thành công
            String successMessage = String.format(
                    "Payment Successful!\n\nOrder Total: $%.2f\nAmount Paid: $%.2f\nChange: $%.2f\nOrder ID: %d",
                    totalCost, amountPaid, changeAmount, currentOrder.getOrderId()
            );
            showAlert(Alert.AlertType.INFORMATION, "Payment Successful", successMessage);

            // Xóa các thông tin liên quan đến đơn hàng sau khi thanh toán
            clearOrder();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update order status: " + e.getMessage());
        }
    }
}

// Phương thức cập nhật trạng thái đơn hàng trong cơ sở dữ liệu
private void updateOrderStatus(int orderId, String status) {
    String query = "UPDATE orders SET status = ? WHERE order_id = ?";
    try (Connection connect = ConnectDB.connectDB();
         PreparedStatement stmt = connect.prepareStatement(query)) {
        stmt.setString(1, status);
        stmt.setInt(2, orderId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update order status: " + e.getMessage());
    }
}



private boolean processOrderItems() {
    boolean success = true;
    try (Connection connect = ConnectDB.connectDB()) {
        for (OrderItem item : orderItems) {
            String updateQuantitySQL = "UPDATE products SET quantity = quantity - ? WHERE product_id = ?";
            try (PreparedStatement stmt = connect.prepareStatement(updateQuantitySQL)) {
                stmt.setInt(1, item.getQuantity());
                stmt.setInt(2, item.getProductId());
                int affectedRows = stmt.executeUpdate();

                if (affectedRows == 0) {
                    success = false;
                    break;
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        success = false;
    }
    return success;
}


    private void clearOrder() {
        orderItems.clear();

        total.setText("0.00$");
        amount.clear();
        change.setText("0.00$");

        currentOrder = new Order(
                0,
                1,
                employeeId,
                0.0,
                "PENDING",
                new java.sql.Date(System.currentTimeMillis())
        );

        try {
            menuDisplayCard();
                orderItems.clear();
    displayTotal();
    tableView.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

@FXML
private void handleRemove(ActionEvent event) {
    OrderItem selectedItem = tableView.getSelectionModel().getSelectedItem();
    if (selectedItem != null) {
        // Bước 1: Xóa `OrderItem` khỏi cơ sở dữ liệu
        try (Connection connect = ConnectDB.connectDB()) {
            String deleteOrderItemQuery = "DELETE FROM order_items WHERE order_item_id = ?";
            try (PreparedStatement stmt = connect.prepareStatement(deleteOrderItemQuery)) {
                stmt.setInt(1, selectedItem.getOrderItemId());
                stmt.executeUpdate();
            }

            // Bước 2: Xóa `OrderItem` khỏi danh sách hiển thị
            orderItems.remove(selectedItem);
            displayTotal();

            // Bước 3: Kiểm tra nếu không còn mục nào trong đơn hàng
            if (orderItems.isEmpty()) {
                // Xóa `Order` khỏi cơ sở dữ liệu nếu không còn mục nào
                String deleteOrderQuery = "DELETE FROM orders WHERE order_id = ?";
                try (PreparedStatement stmt = connect.prepareStatement(deleteOrderQuery)) {
                    stmt.setInt(1, selectedItem.getOrderId());
                    stmt.executeUpdate();
                }
                showAlert(Alert.AlertType.INFORMATION, "Order Removed", "The order has been removed as it contains no items.");
                clearOrder(); // Xóa đơn hàng khỏi màn hình
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Item Removed", "The selected item has been removed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove the item: " + e.getMessage());
        }
    } else {
        showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to remove.");
    }
}


    private int getid;

    @FXML
    private void SelectOrder() {
        OrderItem item = tableView.getSelectionModel().getSelectedItem();
        int num = tableView.getSelectionModel().getSelectedIndex();

        if (item != null && num >= 0) {
            // Process selected OrderItem
            System.out.println("Selected Product: " + item.getProductName());
        } else {
            showAlert(Alert.AlertType.WARNING, "No Product Selected", "Please select a product to view its details.");
        }
    }

    
    public double getTotal() {
        return cardListData.stream().mapToDouble(product -> product.getPrice() * product.getQuantity()).sum();
    }

    public void displayTotal() {
        total.setText(String.format("%.2f", getTotal()) + "$");
    }

}
